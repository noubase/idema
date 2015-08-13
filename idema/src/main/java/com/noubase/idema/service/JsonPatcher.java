package com.noubase.idema.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.noubase.idema.validation.JsonPatchValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.io.IOException;
import java.util.List;

/**
 * Created by rshuper on 30.10.14.
 * © egocube.com
 */
@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class JsonPatcher {

    private ObjectMapper mapper;

    private LocalValidatorFactoryBean validator;

    private JsonPatchValidator jsonValidator;

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setValidator(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }

    @Autowired
    public void setJsonValidator(JsonPatchValidator jsonValidator) {
        this.jsonValidator = jsonValidator;
    }

    public JsonPatcher() {
    }

    public <T> T patch(T target, List<JsonPatchOperation> operations, @NotNull Class<T> tClass) throws BindException, JsonPatchException {

        T patched;
        try {
            if (!jsonValidator.validate(tClass, operations)) {
                throw new JsonPatchException("Target is not patchable");
            }
            JsonNode node = mapper.convertValue(operations, ArrayNode.class);
            JsonPatch patch = JsonPatch.fromJson(node);
            patched = mapper.treeToValue(patch.apply(mapper.valueToTree(target)), tClass);
        } catch (@NotNull JsonPatchException | IOException e) {
            throw new JsonPatchException(e.getMessage());
        }

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(patched, tClass.getCanonicalName());
        validator.validate(patched, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return patched;
    }
}
