package com.a1stream.master.facade;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.bo.master.CMQ020101BO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.master.service.CMQ0201Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
@Component
public class CMQ0201Facade {

    @Resource
    private CMQ0201Service cmq0201Service;

    private static final String FILE_NAME      = "YMVN Accessory E-catalog.pdf";
    private static final String CHAR_PDF       = "pdf";
    private static final String CHAR_ROLE_ID   = "roleId";

    public CMQ020101BO init(String siteId,String userId){

        CMQ020101BO bo = new CMQ020101BO();

        bo.setUploadFlag(true);
        List<String> types = new ArrayList<>();
        types.add(PJConstants.RoleType.SYSSE);
        types.add(PJConstants.RoleType.SALESCOMPANY);  
        List<Long> ids = cmq0201Service.getRoleIds(PJConstants.RoleCode.YMVNSP, types);

        SysUserAuthorityVO sysUserAuthority = cmq0201Service.getSysUserAuthority(siteId, userId);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(sysUserAuthority.getRoleList());
            Set<String> roleIdSet = new HashSet<>();
            for (JsonNode node : jsonNode) {
                roleIdSet.add(node.get(CHAR_ROLE_ID).asText());
            }

            for (Long number : ids) {
                if (roleIdSet.contains(number.toString())) {
                    bo.setUploadFlag(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bo;
    }

    public void upload(MultipartFile file) {

        String targetDirectory = cmq0201Service.getUploadAccCatalog(PJConstants.UploadAccCatalogType.PDF_URL).getParameterValue(); //从DB中获取
        int lastDotIndex = file.getOriginalFilename().lastIndexOf(CommonConstants.CHAR_DOT);
        if (lastDotIndex == -1) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10313", new String[]{}));
        }
        String extension = file.getOriginalFilename().substring(lastDotIndex + 1);
        if (!extension.equalsIgnoreCase(CHAR_PDF)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10313", new String[]{}));
        }
        savePdfFile(file, targetDirectory);

    }

    public void savePdfFile(MultipartFile file, String targetDirectory){

        if (!file.isEmpty()) {

            try {
                // 构建目标文件路径
                Path destinationPath = Paths.get(targetDirectory, FILE_NAME);
                // 如果文件已存在，则删除旧文件
                if (Files.exists(destinationPath)) {
                    Files.delete(destinationPath);
                }
                // 保存文件到目标目录
                try (FileOutputStream fos = new FileOutputStream(destinationPath.toFile())) {
                    fos.write(file.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
