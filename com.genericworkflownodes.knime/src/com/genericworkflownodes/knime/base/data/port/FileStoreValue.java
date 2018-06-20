package com.genericworkflownodes.knime.base.data.port;

import javax.swing.Icon;

import org.knime.base.node.preproc.setoperator.GeneralDataValueComparator;
import org.knime.core.data.DataValue;
import org.knime.core.data.DataValueComparator;
import org.knime.core.data.ExtensibleUtilityFactory;
import org.knime.core.data.convert.DataValueAccessMethod;

public interface FileStoreValue extends DataValue {
    /** Meta information to this value type.
     * @see DataValue#UTILITY
     */
    UtilityFactory UTILITY = new FileStoreUtilityFactory();

    /**
     * @return A port object with the files stored in this value.
     */
    @DataValueAccessMethod(name = "Port Object")
    AbstractFileStoreURIPortObject getPortObject();

    /**
     * @return A description of the content of the file store
     */
    String getDescription();
    
    /** Implementations of the meta information of this value class. */
    class FileStoreUtilityFactory extends ExtensibleUtilityFactory {
        /** Singleton icon to be used to display this cell type. */
        private static final Icon ICON = loadIcon(
                FileStoreValue.class, "/icon/filestoreicon.png");

        /** Only subclasses are allowed to instantiate this class. */
        protected FileStoreUtilityFactory() {
            super(FileStoreValue.class);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Icon getIcon() {
            return ICON;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected DataValueComparator getComparator() {
            return GeneralDataValueComparator.getInstance();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return "Files";
        }
    }
}
