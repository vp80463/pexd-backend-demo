package com.a1stream.web.app.controller.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.FilesUploadReturnBO;
import com.a1stream.common.facade.FileUploadFacade;
import com.a1stream.common.model.BaseResult;
import com.a1stream.common.model.FileUploadForm;
import com.a1stream.domain.vo.CmmPromotionOrderZipHistoryVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author dong zhen
 */
@RestController
@RequestMapping("public/fileOperation")
public class FileOperationController implements RestProcessAware {

    @jakarta.annotation.Resource
    private FileUploadFacade fileUploadFacade;

    /**
     * 根据业务类型和混淆名称获取文件
     * <p>
     * 此方法用于通过URL获取特定业务类型和混淆名称的文件。它首先通过fileUploadFacade获取文件URL，
     * 然后使用该URL创建Resource对象并返回给客户端。如果文件类型无法识别，它将默认使用
     * "application/octet-stream"作为内容类型。如果文件不存在或发生IO异常，方法将返回一个404未找到响应。
     *
     * @param businessType 业务类型，用于区分不同业务领域的文件
     * @param confusionName 混淆名称，文件的唯一标识符
     * @return ResponseEntity，包含文件资源和适当HTTP响应头的响应实体
     */
    @GetMapping(value = "/getFile/{businessType}/{confusionName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable(name = "businessType") String businessType
            , @PathVariable(name = "confusionName") String confusionName) {

        try {
            String url = fileUploadFacade.getFileUrl(confusionName, businessType);
            Path filePath = Paths.get(url).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + confusionName + "\"")
                    .body(resource);
        } catch ( IOException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除文件的接口方法
     *
     * @param businessType 业务类型，用于区分不同业务领域的文件操作
     * @param confusionName 文件的混淆名称，保证文件路径的唯一性
     * @return BaseResult，操作结果的基础响应
     */
    @DeleteMapping(value = "/deleteFile/{businessType}/{confusionName:.+}")
    public BaseResult deleteFile(@PathVariable(name = "businessType") String businessType
            , @PathVariable(name = "confusionName") String confusionName) {

        BaseResult result = new BaseResult();
        try {
            String url = fileUploadFacade.getFileUrl(confusionName, businessType);
            Path filePath = Paths.get(url).normalize();

            if (Files.exists(filePath) && Files.deleteIfExists(filePath)) {
                fileUploadFacade.deleteFileUrl(confusionName, businessType);
                result.setMessage(BaseResult.SUCCESS_MESSAGE);
            } else {
                throw new BusinessCodedException(BaseResult.GET_FILE_ERROR_MESSAGE);
            }
        } catch (NoSuchFileException ex) {
            // Handle specific exception for file not found
            throw new BusinessCodedException("File not found: " + ex.getMessage());
        } catch (IOException ex) {
            // Handle other IO exceptions
            throw new BusinessCodedException("Error deleting file: " + ex.getMessage());
        }
        return result;
    }

    /**
     * 删除文件操作
     * <p>
     * 通过POST请求接收文件上传表单和用户认证信息，用于删除特定的文件
     * 如果业务类型或混淆名称为空，则抛出请求错误异常
     * 尝试获取文件URL并删除文件，如果文件存在且成功删除，则更新数据库中的文件URL并返回成功消息
     * 如果文件不存在，则抛出文件未找到异常
     * 其他IO异常情况下，抛出通用的删除文件错误异常
     *
     * @param form 文件上传表单数据，包含业务类型和混淆名称
     * @param uc 用户认证信息，用于校验用户权限（未在代码中直接使用）
     * @return 返回基础结果对象，包含操作状态和消息
     * @throws BusinessCodedException 自定义业务异常，处理各种错误情况
     */
    @PostMapping(value = "/deleteFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult deleteFiles(@RequestBody final FileUploadForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        if (StringUtils.isEmpty(form.getBusinessType()) || StringUtils.isEmpty(form.getConfusionName())){
            throw new BusinessCodedException(BaseResult.REQUEST_ERROR_MESSAGE);
        }
        BaseResult result = new BaseResult();
        try {
            String confusionName = form.getConfusionName();
            String businessType = form.getBusinessType();

            String url = fileUploadFacade.getFileUrl(confusionName, businessType);
            Path filePath = Paths.get(url).normalize();

            if (Files.exists(filePath) && Files.deleteIfExists(filePath)) {
                fileUploadFacade.deleteFileUrl(confusionName, businessType);
                result.setMessage(BaseResult.SUCCESS_MESSAGE);
            } else {
                throw new BusinessCodedException(BaseResult.GET_FILE_ERROR_MESSAGE);
            }
        } catch (NoSuchFileException ex) {
            // Handle specific exception for file not found
            throw new BusinessCodedException("File not found: " + ex.getMessage());
        } catch (IOException ex) {
            // Handle other IO exceptions
            throw new BusinessCodedException("Error deleting file: " + ex.getMessage());
        }
        return result;
    }

    /**
     * 根据 URL 中提供的业务类型和混淆名称下载文件。
     *
     * @param request 当前的 HTTP 请求对象，用于获取请求头。
     * @param response 当前的 HTTP 响应对象，用于添加响应头。
     * @param businessType 业务类型，用于指定要下载的文件类别。
     * @param confusionName 混淆名称，用于标识要下载的文件。
     * @return 通过 HTTP 响应将文件资源返回给调用者。
     *
     * 注意：此方法涉及文件操作和 HTTP 头的使用，需要注意安全性和错误处理。
     */
    @GetMapping(value = "/downloadFile/{businessType}/{confusionName:.+}")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request
            , HttpServletResponse response
            , @PathVariable(name = "businessType") String businessType
            , @PathVariable(name = "confusionName") String confusionName) {

        try {
            // Check if the header "X-Custom-Header" is present in the request
            String customHeader = request.getHeader("X-Custom-Header");

            if (customHeader!= null) {
                // Add the header "X-Custom-Header" to the response
                response.addHeader("X-Custom-Header", customHeader);
            }

            FilesUploadReturnBO filesUploadReturnBO = fileUploadFacade.getFileUrlAndOriName(confusionName, businessType);
            Path filePath = Paths.get(filesUploadReturnBO.getUrl()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                String actualDownloadName = (filesUploadReturnBO.getName() != null) ? filesUploadReturnBO.getName() : confusionName;
                if (!actualDownloadName.matches("^[\\w\\-.() ]+$")) {
                    actualDownloadName = confusionName;
                }
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + actualDownloadName + "\"");
                headers.add(actualDownloadName, URLEncoder.encode(actualDownloadName, StandardCharsets.UTF_8));
                headers.add("Access-Control-Expose-Headers", "Content-Disposition");
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .headers(headers)
                        .body(resource);
            } else {
                throw new BusinessCodedException(BaseResult.GET_FILE_ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            throw new BusinessCodedException(BaseResult.GET_FILE_ERROR_MESSAGE);
        }
    }

    @PostMapping(value = "/listImgUrl.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult listImageUrl(@RequestBody final FileUploadForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        result.setData(fileUploadFacade.getImageUrlList(form));
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    /**
     * 根据促销订单ID从SFTP获取文件
     * <p>
     * 此方法用于从SFTP服务器检索与特定促销订单相关的文件它通过促销订单的唯一标识符（promotionOrderZipHistoryId）
     * 获取文件，然后将文件作为HTTP响应的主体返回
     *
     * @param promotionOrderZipHistoryId 促销订单的唯一标识符，用于定位特定的文件
     * @return 返回包含文件资源的HTTP响应，包括文件流和相关头信息
     * @throws BusinessCodedException 如果promotionOrderZipHistoryId为null，表示无法定位文件，抛出业务异常
     */
    @GetMapping(value = "/getFileBySftpForPromotionOrder.json")
    public ResponseEntity<Resource> getFileBySftpForPromotionOrder(@RequestParam("promotionOrderZipHistoryId") Long promotionOrderZipHistoryId) {

        if (promotionOrderZipHistoryId == null){
            throw new BusinessCodedException(BaseResult.GET_FILE_ERROR_MESSAGE);
        }
        CmmPromotionOrderZipHistoryVO cmmPromotionOrderZipHistoryVO = fileUploadFacade.getCmmPromotionOrderZipHistoryVO(promotionOrderZipHistoryId);
        String fileUrl = cmmPromotionOrderZipHistoryVO.getZipPath() + cmmPromotionOrderZipHistoryVO.getZipNm();
        byte[] fileBytes = fileUploadFacade.downloadFileFromSftp(fileUrl);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cmmPromotionOrderZipHistoryVO.getZipNm() + "\"");
        headers.add(cmmPromotionOrderZipHistoryVO.getZipNm(), URLEncoder.encode(cmmPromotionOrderZipHistoryVO.getZipNm(), StandardCharsets.UTF_8));
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private static final String FILE_NAME    = "YMVN Accessory E-catalog.pdf";

    @RequestMapping(value = "/link.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileSystemResource> link() {

        // 文件的实际路径，这里应替换为相对路径或通过配置动态获取
        String filePath = fileUploadFacade.getPdfFile()+"/"+FILE_NAME;
        
        // 使用FileSystemResource读取文件
        FileSystemResource file = new FileSystemResource(filePath);
        
        // 设置响应头，通知浏览器以inline模式打开PDF（即在浏览器中直接显示）
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=accessoryCatalog.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(file);
    }

    @PostMapping(value = "/init.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> init(@AuthenticationPrincipal final PJUserDetails uc) {

        Path filePath = Paths.get(fileUploadFacade.getPdfFile(), FILE_NAME);

        try {
            Resource resource = new UrlResource(filePath.toUri());
        
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(resource); 
        } catch (Exception e) {
            throw new BusinessCodedException(BaseResult.GET_FILE_ERROR_MESSAGE);
        }
        
    }
}