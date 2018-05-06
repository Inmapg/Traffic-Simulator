package pr6.view.dialog;

import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Represents a general type of list.
 *
 * @param <E> Generic
 */
public class ListModel<E> extends DefaultListModel<E> {

    List<E> list = null;

    /**
     * Class constructor.
     */
    ListModel() {
    }

    /**
     * Sets the content of the list.
     *
     * @param list
     */
    public void setList(List<E> list) {
        this.list = list;
        refresh();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E getElementAt(int index) {
        return list.get(index);
    }

    @Override
    public int getSize() {
        return list == null ? 0 : list.size();
    }

    /**
     * Refresh the list.
     */
    public void refresh() {
        fireContentsChanged(this, 0, list.size());
    }
}
