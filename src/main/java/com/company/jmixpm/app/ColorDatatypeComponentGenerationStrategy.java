package com.company.jmixpm.app;

import com.company.jmixpm.datatype.ColorDatatype;
import io.jmix.core.JmixOrder;
import io.jmix.core.MetadataTools;
import io.jmix.core.metamodel.datatype.Datatype;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.ColorPicker;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.ComponentGenerationContext;
import io.jmix.ui.component.ComponentGenerationStrategy;
import io.jmix.ui.component.data.ValueSource;
import org.springframework.core.Ordered;

import javax.annotation.Nullable;

@org.springframework.stereotype.Component("demo_ColorDatatypeComponentGenerationStrategy")
public class ColorDatatypeComponentGenerationStrategy implements ComponentGenerationStrategy, Ordered {
    private final UiComponents uiComponents;
    private final MetadataTools metadataTools;

    public ColorDatatypeComponentGenerationStrategy(UiComponents uiComponents,
                                                    MetadataTools metadataTools) {
        this.uiComponents = uiComponents;
        this.metadataTools = metadataTools;
    }

    @Nullable
    @Override
    public Component createComponent(ComponentGenerationContext context) {
        MetaClass metaClass = context.getMetaClass();
        MetaPropertyPath mpp = metadataTools.resolveMetaPropertyPathOrNull(metaClass, context.getProperty());

        if (mpp != null
                && mpp.getRange().isDatatype()
                && ((Datatype<?>) mpp.getRange().asDatatype()) instanceof ColorDatatype) {
            ColorPicker colorPicker = uiComponents.create(ColorPicker.class);
            colorPicker.setDefaultCaptionEnabled(true);

            ValueSource valueSource = context.getValueSource();
            if (valueSource != null) {
                colorPicker.setValueSource(valueSource);
            }

            return colorPicker;
        }

        return null;
    }

    @Override
    public int getOrder() {
        return JmixOrder.HIGHEST_PRECEDENCE + 10;
    }
}
