package com.example.sqlite0.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sqlite0.data.ItemRepository;
import com.example.sqlite0.models.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends AndroidViewModel {
    private final ItemRepository itemRepository;
    private final MutableLiveData<List<Item>> itemsLiveData = new MutableLiveData<>();

    public ItemViewModel(Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
        itemsLiveData.setValue(new ArrayList<>());
    }

    // Lấy tất cả items theo userId
    public LiveData<List<Item>> getItems(int userId) {
        List<Item> items = itemRepository.getAllItems(userId);
        itemsLiveData.setValue(items);
        return itemsLiveData;
    }

    // Lấy items theo ngày
    public LiveData<List<Item>> getItemsByDate(String date, int userId) {
        List<Item> items = itemRepository.getItemsByDate(date, userId);
        itemsLiveData.setValue(items);
        return itemsLiveData;
    }

    // Thêm item
    public void addItem(Item item, int userId) {
        itemRepository.addItem(item, userId);
        // Cập nhật LiveData sau khi thêm
        List<Item> currentItems = itemsLiveData.getValue();
        if (currentItems != null) {
            currentItems.add(item);
            itemsLiveData.setValue(currentItems);
        }
    }

    // Cập nhật item
    public void updateItem(Item item) {
        itemRepository.updateItem(item);
        // Cập nhật LiveData
        List<Item> currentItems = itemsLiveData.getValue();
        if (currentItems != null) {
            for (int i = 0; i < currentItems.size(); i++) {
                if (currentItems.get(i).getId() == item.getId()) {
                    currentItems.set(i, item);
                    break;
                }
            }
            itemsLiveData.setValue(currentItems);
        }
    }

    // Xóa item
    public void deleteItem(int itemId) {
        itemRepository.deleteItem(itemId);
        // Cập nhật LiveData
        List<Item> currentItems = itemsLiveData.getValue();
        if (currentItems != null) {
            currentItems.removeIf(item -> item.getId() == itemId);
            itemsLiveData.setValue(currentItems);
        }
    }
}