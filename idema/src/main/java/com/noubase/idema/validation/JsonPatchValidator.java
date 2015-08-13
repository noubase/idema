package com.noubase.idema.validation;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonpatch.*;
import com.noubase.idema.annotation.Unchangeable;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.noubase.idema.util.AnnotationUtil.getFieldsByAnnotation;

/**
 * Created by rshuper on 30.10.14.
 * © egocube.com
 */
@Component
public class JsonPatchValidator {

    public <T> boolean validate(Class<T> tClass, @NotNull List<JsonPatchOperation> operations) {

        Set<String> fields = getFieldsByAnnotation(tClass, Unchangeable.class);
        for (String field : fields) {
            for (JsonPatchOperation operation : operations) {
                boolean valid = true;
                if (operation instanceof RemoveOperation) {
                    valid = !equals(field, operation.getPath());
                } else if (operation instanceof PathValueOperation) {
                    valid = !equals(field, operation.getPath());
                } else if (operation instanceof DualPathOperation) {
                    switch (operation.getOp()) {
                        case MoveOperation.OP:
                            valid = !(equals(field, ((DualPathOperation) operation).getFrom())
                                    || equals(field, operation.getPath()));
                            break;
                        case CopyOperation.OP:
                            valid = !equals(field, operation.getPath());
                            break;
                    }
                }
                if (!valid) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean equals(@NotNull String field, @NotNull JsonPointer path) {
        return path.toString().indexOf(field) == 1;
    }
}
