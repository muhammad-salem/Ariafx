package ariafx.opt;

import ariafx.core.url.Item;

public class TempItems {

    private Item[] items;

    public TempItems() {
    }

    public TempItems(Item[] items) {
        this.items = items;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }
}
