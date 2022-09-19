package com.bignerdranch.android.navigationcomponenttabs.screens.main.tabs.admin

import androidx.recyclerview.widget.DiffUtil
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.entities.BoxAndSettings

class AdminTreeItemDiffCallback(
    private val oldList: List<AdminTreeItem>,
    private val newList: List<AdminTreeItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}