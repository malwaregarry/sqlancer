package sqlancer.common.gen;

import sqlancer.common.schema.TableColumn;

import java.util.List;

public interface TLPGenerator<U> extends SelectGenerator<U>, PredicateGenerator<U> {

    <C extends TableColumn<U>> void setColumns(List<C> columns);

}
