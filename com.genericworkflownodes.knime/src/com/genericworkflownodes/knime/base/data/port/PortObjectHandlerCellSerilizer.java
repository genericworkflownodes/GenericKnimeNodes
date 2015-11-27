package com.genericworkflownodes.knime.base.data.port;

import java.io.IOException;

import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;

/**
 * DataCellSerializer for PortObjectHandlerCell.
 * 
 * @author aiche
 */
public class PortObjectHandlerCellSerilizer implements
        DataCellSerializer<PortObjectHandlerCell> {

    @Override
    public void serialize(PortObjectHandlerCell cell,
            DataCellDataOutput output) throws IOException {
        cell.save(output);
    }

    @Override
    public PortObjectHandlerCell deserialize(DataCellDataInput input)
            throws IOException {
        PortObjectHandlerCell cell = new PortObjectHandlerCell();
        cell.load(input);
        return cell;
    }

}
