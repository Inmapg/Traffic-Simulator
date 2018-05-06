package pr6.view.dialog;

import java.util.List;

import javax.swing.DefaultListModel;

public class ListModel<E> extends DefaultListModel<E> {

    List<E> _list;

    ListModel() {
        _list = null;
    }

    public void setList(List<E> l) {
        _list = l;
        refresh();
    }

    @Override
    public E get(int index) {
        return _list.get(index);
    }

    @Override
    public E getElementAt(int index) {
        return _list.get(index);
    }

    @Override
    public int getSize() {
        return _list == null ? 0 : _list.size();
    }

    public void refresh() {
        fireContentsChanged(this, 0, _list.size());
    }

}
