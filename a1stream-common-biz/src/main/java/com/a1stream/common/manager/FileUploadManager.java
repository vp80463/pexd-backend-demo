package com.a1stream.common.manager;

import com.a1stream.common.bo.FilesUploadReturnBO;
import com.a1stream.common.bo.FilesUploadUtilBO;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.entity.CmmUploadFile;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.repository.CmmUploadFileRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author dong zhen
 */
@Slf4j
@Service
public class FileUploadManager {

    @Resource
    private SystemParameterRepository systemParameterRepository;

    @Resource
    private CmmUploadFileRepository cmmUploadFileRepository;

    /**
     * 允许上传的文件后缀名
     */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".jpg", ".png", ".pdf", ".xml", ".txt", ".xlsx", ".xls", ".pptx", ".jpeg", ".zip"
    ));

    /**
     * 处理多文件上传
     * <p>
     * 此方法接收一个包含文件上传实用程序的参数对象，用于处理文件上传的相关逻辑
     * 它会根据业务类型查找系统参数，以确定文件的保存路径然后确保此路径对应的目录存在
     * 接着，方法会遍历每一个待上传的文件，处理每个文件的上传过程，并将上传信息保存至列表中
     * 最后，批量保存上传信息至数据库，并返回一个包含旧文件名和新文件名映射的Map
     *
     * @param form 包含文件上传所需信息的实用程序对象，包括业务类型和多个文件
     * @return 包含旧文件名和新文件名的映射，以便在需要时可以查找或替换文件
     * @throws BusinessCodedException 当目录创建失败或文件处理失败时，抛出自定义业务异常
     */
    public Map<String, String> multipleFileUpload(FilesUploadUtilBO form) {

        Map<String, String> oldAndNewFileNameMap = new HashMap<>();
        SystemParameter systemParameter = systemParameterRepository.findBySystemParameterTypeId(form.getBusinessType());
        String path = systemParameter.getParameterValue();

        try {
            ensureDirectoryExists(path);
        } catch (IOException e) {
            log.error("Multiple File Failed to create directory: {}", e.getMessage());
            throw new BusinessCodedException("File Failed to create directory");
        }

        List<CmmUploadFile> uploadFilesList = new ArrayList<>();
        File directory = new File(path);

        for (MultipartFile file : form.getMultipleFiles()) {
            try {
                processFileUpload(file, form, directory, uploadFilesList, oldAndNewFileNameMap);
            } catch (IOException | BusinessCodedException e) {
                log.error("Multiple File Error processing file upload: {}", e.getMessage());
                throw new BusinessCodedException("Failed to process file upload");
            }
        }
        cmmUploadFileRepository.saveInBatch(uploadFilesList);
        return oldAndNewFileNameMap;
    }

    /**
     * 单文件上传
     * <p>
     * 该方法首先根据业务类型获取系统参数，确定文件上传路径，确保目录存在后，将文件上传到指定目录，
     * 并保存上传文件的信息到数据库。如果过程中遇到IO错误或业务异常，将记录错误并抛出自定义异常。
     *
     * @param form 文件上传参数对象，包含待上传文件及业务类型等信息
     * @return 包含旧文件名和新文件名映射的字符串映射对象
     * @throws BusinessCodedException 当目录创建失败或文件处理失败时，抛出自定义业务异常
     */
    public Map<String, String> singleFileUpload(FilesUploadUtilBO form) {

        Map<String, String> oldAndNewFileNameMap = new HashMap<>();
        SystemParameter systemParameter = systemParameterRepository.findBySystemParameterTypeId(form.getBusinessType());
        String path = systemParameter.getParameterValue();

        try {
            ensureDirectoryExists(path);
        } catch (IOException e) {
            log.error("Single File Failed to create directory: {}", e.getMessage());
            throw new BusinessCodedException(BaseResult.ERROR_MESSAGE);
        }

        List<CmmUploadFile> uploadFilesList = new ArrayList<>();
        File directory = new File(path);

        try {
            processFileUpload(form.getSingleFile(), form, directory, uploadFilesList, oldAndNewFileNameMap);
        } catch (IOException | BusinessCodedException e) {
            log.error("Single File Error processing file upload: {}", e.getMessage());
            throw new BusinessCodedException("Failed to process file upload");
        }

        cmmUploadFileRepository.save(uploadFilesList.get(0));
        return oldAndNewFileNameMap;
    }

    /**
     * 检查文件扩展名是否被允许
     *
     * @param extension 文件扩展名，包括点号
     * @return 如果文件扩展名存在于允许的扩展名列表中，则返回true；否则返回false
     */
    private boolean isAllowedExtension(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 对文件名进行安全性清理
     * 主要移除了文件名中的路径分隔符，以确保该文件名不会被误解释为路径的一部分
     * 这在防止目录遍历攻击中尤为重要，其中攻击者可能尝试通过文件名中的相对路径分隔符来访问受限的目录
     *
     * @param fileName 待清理的文件名
     * @return 清理后的文件名
     */
    private String sanitizeFileName(String fileName) {
        // 移除可能的路径分隔符
        return fileName.replaceAll("[/\\\\]", "");
    }

    /**
     * 确保指定路径的目录存在如果目录不存在，则创建该目录
     *
     * @param path 目录的路径
     * @throws IOException 如果创建目录失败或路径不合法
     */
    private void ensureDirectoryExists(String path) throws IOException {
        File directory = new File(path);
        if (!directory.exists()) {
            Files.createDirectories(directory.toPath());
        }
    }

    /**
     * 处理文件上传方法
     * <p>
     * 此方法负责处理文件的上传和保存过程，包括检查文件是否为空、获取文件流、生成新文件名、验证文件扩展名、
     * 防止路径遍历攻击、保存文件元数据、以及将文件保存到指定目录等操作
     *
     * @param file 待上传的文件
     * @param form 文件上传的业务信息
     * @param directory 文件保存的目录
     * @param uploadFilesList 保存文件元数据的列表
     * @param oldAndNewFileNameMap 保存原始文件名和新文件名映射的Map
     * @throws IOException 如果文件操作出错
     * @throws BusinessCodedException 如果业务处理过程出错或文件扩展名不合法
     */
    private void processFileUpload(MultipartFile file, FilesUploadUtilBO form, File directory, List<CmmUploadFile> uploadFilesList, Map<String, String> oldAndNewFileNameMap) throws IOException, BusinessCodedException {
        // 检查文件是否为空
        if (!file.isEmpty()) {
            try (InputStream is = file.getInputStream()) {
                String oriName = file.getOriginalFilename();
                String fileName;
                String extension = "";
                // 获取文件扩展名
                int lastDotIndex = Objects.requireNonNull(oriName).lastIndexOf('.');
                if (lastDotIndex > 0 && lastDotIndex < oriName.length() - 1) {
                    extension = oriName.substring(lastDotIndex);
                }
                String randomFileName = UUID.randomUUID() + extension;

                // 验证文件扩展名
                if (!isAllowedExtension(extension)) {
                    log.error("Invalid file extension: {}", extension);
                    throw new BusinessCodedException("Invalid file extension");
                }

                // 防止路径遍历攻击，确保文件名的安全性
                String safeFileName = sanitizeFileName(randomFileName);

                // 根据业务规则名称是否为空，决定使用安全文件名还是根据业务规则生成文件名
                if (StringUtils.isEmpty(form.getBusinessRulesName())) {
                    fileName = safeFileName;
                } else {
                    fileName = form.getBusinessRulesName() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + "_" + Instant.now().getNano() + extension;
                }

                // 创建CmmUploadFile对象，保存文件元数据
                CmmUploadFile cmmUploadFile = new CmmUploadFile();
                cmmUploadFile.setSiteId(form.getSiteId());
                cmmUploadFile.setUploadDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
                cmmUploadFile.setUploadPath(directory.getAbsolutePath());
                cmmUploadFile.setBusinessId(form.getBusinessId());
                cmmUploadFile.setBusinessType(form.getBusinessType());
                cmmUploadFile.setOriFileName(oriName);
                cmmUploadFile.setFileName(fileName);
                cmmUploadFile.setConfusionName(safeFileName);

                // 将文件流保存到目标文件系统位置
                File destFile = new File(directory, fileName);
                Files.copy(is, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // 将文件元数据添加到列表和映射中，以便后续处理
                uploadFilesList.add(cmmUploadFile);
                oldAndNewFileNameMap.put(oriName, fileName);
            }
        }
    }

    /**
     * 根据业务类型获取文件URL列表
     * <p>
     * 此方法通过接收业务类型参数，从数据库中查询与该业务类型相关的文件记录，并生成每个文件的访问URL
     * 它首先从数据库中获取特定业务类型的上传文件列表，然后获取系统参数中用于文件访问的基础URL，
     * 最后，结合基础URL和文件的业务类型及混淆名称，构造出每个文件的完整访问URL，返回包含这些URL的列表
     *
     * @param businessType 业务类型，用于定位特定业务类型的上传文件
     * @return 返回一个FilesUploadReturnBO对象列表，其中每个对象包含文件的URL和名称
     */
    public List<FilesUploadReturnBO> getUrlListByBusinessType(String businessType) {

        List<CmmUploadFile> cmmUploadFileList = cmmUploadFileRepository.findByBusinessType(businessType);
        SystemParameter systemParameter = systemParameterRepository.findBySystemParameterTypeId(MstCodeConstants.SystemParameterType.GETFILEBASEURL);

        List<FilesUploadReturnBO> returnList = new ArrayList<>();
        for (CmmUploadFile member : cmmUploadFileList) {
            FilesUploadReturnBO filesUploadReturnBO = new FilesUploadReturnBO();
            filesUploadReturnBO.setUrl(systemParameter.getParameterValue() + member.getBusinessType() + "/" + member.getConfusionName());
            filesUploadReturnBO.setName(member.getConfusionName());
            returnList.add(filesUploadReturnBO);
        }
        return returnList;
    }

    /**
     * 根据业务id获取文件URL列表
     * <p>
     * 此方法通过接收业务类型参数，从数据库中查询与该业务类型相关的文件记录，并生成每个文件的访问URL
     * 它首先从数据库中获取特定业务类型的上传文件列表，然后获取系统参数中用于文件访问的基础URL，
     * 最后，结合基础URL和文件的业务类型及混淆名称，构造出每个文件的完整访问URL，返回包含这些URL的列表
     *
     * @param businessId 业务类型，用于定位特定业务类型的上传文件
     * @return 返回一个FilesUploadReturnBO对象列表，其中每个对象包含文件的URL和名称
     */
    public List<FilesUploadReturnBO> getUrlListByBusinessId(Long businessId) {

        List<CmmUploadFile> cmmUploadFileList = cmmUploadFileRepository.findByBusinessId(businessId);
        SystemParameter systemParameter = systemParameterRepository.findBySystemParameterTypeId(MstCodeConstants.SystemParameterType.GETFILEBASEURL);

        List<FilesUploadReturnBO> returnList = new ArrayList<>();
        for (CmmUploadFile member : cmmUploadFileList) {
            FilesUploadReturnBO filesUploadReturnBO = new FilesUploadReturnBO();
            filesUploadReturnBO.setUrl(systemParameter.getParameterValue() + member.getBusinessType() + "/" + member.getConfusionName());
            filesUploadReturnBO.setName(member.getConfusionName());
            returnList.add(filesUploadReturnBO);
        }
        return returnList;
    }

    /**
     * 根据业务类型与混淆名称获取文件URL列表
     * <p>
     * 此方法通过接收业务类型参数，从数据库中查询与该业务类型相关的文件记录，并生成每个文件的访问URL
     * 它首先从数据库中获取特定业务类型的上传文件列表，然后获取系统参数中用于文件访问的基础URL，
     * 最后，结合基础URL和文件的业务类型及混淆名称，构造出每个文件的完整访问URL，返回包含这些URL的列表
     *
     * @param businessType 业务类型，用于定位特定业务类型的上传文件
     * @param confusionName 混淆名称，用于定位特定业务类型的上传文件
     * @return 返回一个FilesUploadReturnBO对象列表，其中每个对象包含文件的URL和名称
     */
    public FilesUploadReturnBO getUrlListByBusinessTypeAndConfusionName(String businessType, String confusionName) {

        SystemParameter systemParameter = systemParameterRepository.findBySystemParameterTypeId(MstCodeConstants.SystemParameterType.GETFILEBASEURL);
        CmmUploadFile cmmUploadFile = cmmUploadFileRepository.findByBusinessTypeAndConfusionName(businessType, confusionName);

        FilesUploadReturnBO filesUploadReturnBO = new FilesUploadReturnBO();
        filesUploadReturnBO.setUrl(systemParameter.getParameterValue() + cmmUploadFile.getBusinessType() + "/" + cmmUploadFile.getConfusionName());
        filesUploadReturnBO.setName(cmmUploadFile.getConfusionName());
        return filesUploadReturnBO;
    }
}
