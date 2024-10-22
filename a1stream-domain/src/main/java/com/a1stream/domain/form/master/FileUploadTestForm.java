package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;

/**
 * @author dong zhen
 */
@Setter
@Getter
public class FileUploadTestForm extends BaseForm {

    @Serial
    private static final long serialVersionUID = 6135338517273193700L;

    /**
     * 条件1
     */
    private String condition1;

    /**
     * 类别
     */
    private String businessType;

    /**
     * 文件名
     */
//    private String fileName;

    /**
     * 文件组
     */
    private transient MultipartFile[] files;
}
