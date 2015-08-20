package com.noubase.core.crud.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rshuper on 29.10.14.
 * © egocube.com
 */

public class JsonPatchBuilder {

    private final List<JsonPatchOperation> operations = new ArrayList<>();

    private JsonPatchBuilder() {
    }

    @NotNull
    public List<JsonPatchOperation> getOperations() {
        return operations;
    }

    @NotNull
    public static JsonPatchBuilder create() {
        return new JsonPatchBuilder();
    }

    @NotNull
    public JsonPatchBuilder add(String path, String value) throws JsonPointerException {
        operations.add(new AddOperation(new JsonPointer(path), new TextNode(value)));
        return this;
    }

    @NotNull
    public JsonPatchBuilder copy(String from, String to) throws JsonPointerException {
        operations.add(new CopyOperation(new JsonPointer(from), new JsonPointer(to)));
        return this;
    }

    @NotNull
    public JsonPatchBuilder remove(String path) throws JsonPointerException {
        operations.add(new RemoveOperation(new JsonPointer(path)));
        return this;
    }

    @NotNull
    public JsonPatchBuilder move(String from, String to) throws JsonPointerException {
        operations.add(new MoveOperation(new JsonPointer(from), new JsonPointer(to)));
        return this;
    }


    @NotNull
    public JsonPatchBuilder replace(String path, String value) throws JsonPointerException {
        operations.add(new ReplaceOperation(new JsonPointer(path), new TextNode(value)));
        return this;
    }

    @NotNull
    public JsonPatchBuilder replace(String path, Double value) throws JsonPointerException {
        operations.add(new ReplaceOperation(new JsonPointer(path), new DoubleNode(value)));
        return this;
    }

    @NotNull
    public JsonPatchBuilder replace(String path, boolean value) throws JsonPointerException {
        operations.add(new ReplaceOperation(new JsonPointer(path), BooleanNode.valueOf(value)));
        return this;
    }

    @NotNull
    public JsonPatchBuilder replace(String path, int value) throws JsonPointerException {
        operations.add(new ReplaceOperation(new JsonPointer(path), new IntNode(value)));
        return this;
    }

    @NotNull
    public JsonPatchBuilder replace(String path, @NotNull Collection<?> value) throws JsonPointerException {
        ArrayNode node = new ObjectMapper().createArrayNode();
        for (Object o : value) {
            node.addPOJO(o);
        }
        operations.add(new ReplaceOperation(new JsonPointer(path), node));
        return this;
    }
}
