package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.capability.Capabilities;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;

import java.lang.reflect.Field;
import java.util.Map;

final class CapabilityFilter {

    private final Capabilities capabilities;

    CapabilityFilter(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    void apply(ConfigSection config) {
        final ConfigDeclaration declaration = config.getDeclaration();
        final Map<String, FieldDeclaration> fields = declaration.getFieldMap();

        fields.values().removeIf(this::unsupported);

        // The options are only half of the file; the comments around them are the half an
        // administrator reads, and they document rules and triggers too.
        declaration.setHeader(CommentDirectives.apply(declaration.getHeader(), capabilities));
        fields.values().forEach(field ->
                field.setComment(CommentDirectives.apply(field.getComment(), capabilities))
        );

        config.applyCapabilities(capabilities);
    }

    private boolean unsupported(FieldDeclaration declaration) {
        final Field field = declaration.getField();
        final RequiresCapability required = field.getAnnotation(RequiresCapability.class);

        return required != null && !capabilities.supports(required.value());
    }
}
