package com.a1stream.common.manager;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.jcraft.jsch.*;
import com.ymsl.solid.base.exception.BusinessCodedException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dong zhen
 */
@Slf4j
@Service
public class RunShellManager {

    @Resource
    private SystemParameterRepository systemParameterRepository;

    private static final String SHELL_SERVICE_ADDRESS = "S074SHELLSERVICE";

    /**
     * 执行shell脚本
     *
     * @param shellType shell类型，用于标识shell脚本的类型
     * @param shellNm shell名称，用于标识具体要执行的shell脚本
     */
    public void runShell(String shellType, String shellNm) {

        try {
            List<SystemParameter> systemParameterList = systemParameterRepository .findBySiteIdAndSystemParameterTypeIdIn(CommonConstants.CHAR_DEFAULT_SITE_ID, Arrays.asList(shellType, SHELL_SERVICE_ADDRESS));
            Map<String, String> systemParameterMap = systemParameterList.stream().collect(Collectors.toMap(SystemParameter::getSystemParameterTypeId, SystemParameter::getParameterValue));
            String allowCommands = systemParameterMap.get(shellType) + "/" + shellNm;
            String[] arr = systemParameterMap.get(SHELL_SERVICE_ADDRESS).split("\\|");
            String host = arr[0];
            String user = arr[1];
            int port  = Integer.parseInt(arr[2]);
            String password = arr[3];
            sshExec(host, port, user, password, allowCommands);
        } catch (Exception e) {
            throw new BusinessCodedException(BaseResult.ERROR_MESSAGE);
        }
    }

    /**
     * 执行远程服务器上的命令
     *
     * @param host     远程服务器的主机名或IP地址
     * @param port     远程服务器的SSH端口
     * @param user     远程服务器的用户名
     * @param password 远程服务器的用户密码
     * @param allowCommands 被允许在远程服务器上执行的命令
     * @throws Exception 如果在建立SSH连接或执行命令过程中发生错误
     */
    private void sshExec(String host, int port, String user, String password, String allowCommands) throws Exception {

        JSch jsch = new JSch();
        log.warn("-------------请等待-------------");

        Session session = jsch.getSession(user, host, port);
        log.warn("-------------获取Session-------------");

        jsch.setKnownHosts(host);

        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        try {
            log.warn("-------------尝试远程连接服务器------------- {}@{}", user, host);
            session.connect(60000);
            log.warn("-------------远程服务器连接已建立-------------");
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(allowCommands);
            try {
                InputStream in = channel.getInputStream();
                channel.connect();
                log.warn("-------------成功建立连接通道，开始运行命令-------------");
                StringBuilder sb = new StringBuilder();
                byte[] tmp = new byte[1024];
                long startTime = System.currentTimeMillis();
                // 超时30秒自动断开
                while (System.currentTimeMillis() - startTime < 30000 && !channel.isClosed()) {
                    int i = in.read(tmp, 0, 1024);
                    if (i > 0) {
                        String str = new String(tmp, 0, i);
                        sb.append(str);
                        log.info(str);
                    }
                }
                log.info("命令执行结果：\n{}", sb);
            } catch (JSchException | IOException e) {
                log.error("执行命令时发生错误: ", e);
                throw e;
            } finally {
                channel.disconnect();
                log.warn("-------------命令运行成功，连接通道断开-------------");
            }
        } catch (Exception e) {
            log.error("建立SSH连接时发生错误: ", e);
            throw e;
        } finally {
            session.disconnect();
            log.warn("-------------远程连接关闭-------------");
        }
    }

    /**
     * 从SFTP服务器下载文件
     *
     * @param remoteFilePath 远程文件路径
     * @return 文件的字节数组
     */
    public byte[] downloadFileFromSftp(String remoteFilePath) {

        try {
            List<SystemParameter> systemParameterList = systemParameterRepository .findBySiteIdAndSystemParameterTypeIdIn(CommonConstants.CHAR_DEFAULT_SITE_ID, Arrays.asList(SHELL_SERVICE_ADDRESS));
            Map<String, String> systemParameterMap = systemParameterList.stream().collect(Collectors.toMap(SystemParameter::getSystemParameterTypeId, SystemParameter::getParameterValue));
            String[] arr = systemParameterMap.get(SHELL_SERVICE_ADDRESS).split("\\|");
            String host = arr[0];
            String user = arr[1];
            int port  = Integer.parseInt(arr[2]);
            String password = arr[3];
            return this.downloadFile(host, port, user, password, remoteFilePath);
        } catch (Exception e) {
            throw new BusinessCodedException(BaseResult.ERROR_MESSAGE);
        }
    }

    /**
     * 通过SFTP协议下载文件
     *
     * @param host         远程主机IP地址
     * @param port         远程主机端口
     * @param user         远程主机用户名
     * @param password     远程主机密码
     * @param remoteFilePath 远程主机上文件的路径
     * @return 下载的文件内容，以字节数组形式返回
     * @throws JSchException 如果在建立SSH连接时发生错误
     * @throws SftpException 如果在执行SFTP操作时发生错误
     */
    private byte[] downloadFile(String host, int port, String user, String password, String remoteFilePath) throws JSchException, SftpException {

        JSch jsch = new JSch();
        log.warn("--------------请等待--------------");

        Session session = jsch.getSession(user, host, port);
        log.warn("--------------获取Session--------------");

        jsch.setKnownHosts(host);

        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        try {
            log.warn("--------------尝试远程连接服务器-------------- {}@{}", user, host);
            session.connect(60000);

            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            try {
                sftpChannel.connect();

                log.warn("--------------尝试下载文件-------------- {}", remoteFilePath);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                sftpChannel.get(remoteFilePath, outputStream);

                log.warn("--------------文件下载成功--------------");
                return outputStream.toByteArray();
            } finally {
                log.warn("--------------断开SFTP连接--------------");
                sftpChannel.disconnect();
            }
        } catch (JSchException | SftpException e) {
            log.error("建立SSH连接或下载文件时发生错误: ", e);
            throw e;
        } finally {
            log.warn("--------------远程连接关闭--------------");
            session.disconnect();
        }
    }
}
