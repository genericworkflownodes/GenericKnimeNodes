package com.genericworkflownodes.knime.base.data.port;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.renderer.AbstractDataValueRendererFactory;
import org.knime.core.data.renderer.DataValueRenderer;
import org.knime.core.data.renderer.DefaultDataValueRenderer;
import org.knime.core.data.renderer.MultiLineBasicLabelUI;

/**
 * Default renderer for {@link IntValue} and {@link LongValue}.
 *
 * @author wiswedel, University of Konstanz
 */
@SuppressWarnings("serial")
public final class FileStoreValueRenderer extends DefaultDataValueRenderer {
    /**
     * Factory for the {@link FileStoreValueRenderer}.
     *
     * @since 2.8
     */
    public static final class Factory extends AbstractDataValueRendererFactory {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDescription() {
            return DESCRIPTION;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DataValueRenderer createRenderer(final DataColumnSpec colSpec) {
            return new FileStoreValueRenderer();
        }
    }

    private static final String DESCRIPTION = "File Store";

    /**
     * Default Initialization is empty.
     */
    FileStoreValueRenderer() {
        super(DESCRIPTION);
        setUI(new MultiLineBasicLabelUI());
    }

    /**
     * Tries to cast o FileStoreValueRenderer and will set the string in the super class.
     * If that fails, the object's toString() method is used.
     *
     * @param value the object to be rendered, should be an {@link IntValue} or {@link LongValue}
     */
    @Override
    protected void setValue(final Object value) {
        if (value == null) {
            super.setValue("?");
        } if (value instanceof FileStoreValue) {
            super.setValue(((FileStoreValue)value).getDescription());
        } else {
            super.setValue(value);
        }
    }

}
