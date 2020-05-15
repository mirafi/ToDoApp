package com.proit.todo.api.endpoint.restController;

import com.proit.todo.api.dto.task.TaskDetailedDto;
import com.proit.todo.api.dto.task.TaskSummaryDto;
import com.proit.todo.api.mapper.TaskMapper;
import com.proit.todo.core.Form.task.TaskCreateForm;
import com.proit.todo.core.Form.task.TaskSearchForm;
import com.proit.todo.core.Form.task.TaskUpdateForm;
import com.proit.todo.core.constant.Enums;
import com.proit.todo.core.exceptions.UnprocessedEntityException;
import com.proit.todo.core.persistence.entity.Task;
import com.proit.todo.core.service.iface.TaskService;
import com.proit.todo.core.validator.TaskValidator;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/task", produces = { "application/json" })
public class TaskController {

    private TaskService taskService;
    private TaskValidator taskValidator;

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Autowired
    public void setTaskValidator(TaskValidator taskValidator) {
        this.taskValidator = taskValidator;
    }

    @RequestMapping(path = "/get-by-id/{id}",method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable("id") int id){

        Task task = this.taskService.getById(id,true);

        /**
         * Mapping with Data transfer object
         * */
        TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
        TaskDetailedDto taskDetailedDto = taskMapper.toDetailsDto(task);

        return ResponseEntity.ok(taskDetailedDto);
    }

    @RequestMapping(path = "/create",method = RequestMethod.POST)
    public ResponseEntity<?> create(@Valid @RequestBody TaskCreateForm taskCreateForm,
                                    BindingResult result){

        /**
         * Custom form validation
         * */
        this.taskValidator.validateCreate(taskCreateForm,result);
        if(result.hasErrors())throw new UnprocessedEntityException(result);

        /**
         * Creating Task
         * */
        Task task = this.taskService.create(taskCreateForm);

        /**
         * [Detailed]
         * Mapping with Data transfer object
         * */
        TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
        TaskDetailedDto taskDetailedDto = taskMapper.toDetailsDto(task);

        return ResponseEntity.ok(taskDetailedDto);
    }

    @RequestMapping(path = "/update",method = RequestMethod.POST)
    public ResponseEntity<?> update(@Valid @RequestBody TaskUpdateForm taskUpdateForm,
                                    BindingResult result){

        /**
         * Custom form validation
         * */
        this.taskValidator.validateUpdate(taskUpdateForm,result);
        if(result.hasErrors())throw new UnprocessedEntityException(result);


        /**
         * Updating Task
         * */
        Task task = this.taskService.update(taskUpdateForm);

        /**
         * [Detailed]
         * Mapping with Data transfer object
         * */
        TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
        TaskDetailedDto taskDetailedDto = taskMapper.toDetailsDto(task);

        return ResponseEntity.ok(taskDetailedDto);
    }

    @RequestMapping(path = "/mark-as-completed/{id}",method = RequestMethod.POST)
    public ResponseEntity<?> markAsCompleted(@PathVariable("id") int id){

        /**
         * Updating state -> DONE
         * */
        Task task = this.taskService.updateState(id, Enums.TASK_STATE.DONE);

        /**
         * [Summery]
         * Mapping with Data transfer object
         * */
        TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
        TaskSummaryDto taskSummaryDto = taskMapper.toSummeryDto(task);

        return ResponseEntity.ok(taskSummaryDto);
    }

    @RequestMapping(path = "/mark-as-new/{id}",method = RequestMethod.POST)
    public ResponseEntity<?> markAsNew(@PathVariable("id") int id){

        /**
         * Updating state -> NEW
         * */
        Task task = this.taskService.updateState(id, Enums.TASK_STATE.NEW);

        /**
         * [Summery]
         * Mapping with Data transfer object
         * */
        TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
        TaskSummaryDto taskSummaryDto = taskMapper.toSummeryDto(task);

        return ResponseEntity.ok(taskSummaryDto);
    }

    @RequestMapping(path = "/get-all",method = RequestMethod.GET)
    public ResponseEntity<?> getAll(@Valid TaskSearchForm taskSearchForm,BindingResult result){

        /**
         * Form error
        * */
        if(result.hasErrors())throw new UnprocessedEntityException(result);


        /**
         * Getting task list by search criteria
         * */
        Page<Task> taskPage = this.taskService.getAllBySearchCriteria(taskSearchForm);


        /**
         * [Summery List]
         * Mapping with Data transfer object
         * */
        TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
        Page<TaskSummaryDto> taskSummaryDtos = taskPage.map(taskMapper::toSummeryDto);

        return ResponseEntity.ok(taskSummaryDtos);
    }

}