package com.a1stream.web.app.controller.common;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.ToDoListBO;
import com.a1stream.common.facade.ToDoListFacade;
import com.a1stream.common.model.ToDoListForm;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("master/todolist")
public class ToDoListController implements RestProcessAware{

    @Resource
    private ToDoListFacade toDoListFacade;

    @PostMapping(value = "/queryOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ToDoListBO> queryOrder(@RequestBody final ToDoListForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return toDoListFacade.queryOrder(model,uc.getDealerCode());
    }
}