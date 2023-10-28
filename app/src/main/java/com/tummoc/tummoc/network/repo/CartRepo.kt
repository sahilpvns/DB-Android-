package com.tummoc.tummoc.network.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tummoc.tummoc.utils.CoroutineUtils
import com.tummoc.tummoc.network.db.TummocRoomDatabase
import com.tummoc.tummoc.network.db.entity.MenuInfo
import com.tummoc.tummoc.network.db.entity.UserCart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CartRepo {
    private val scope = CoroutineScope(Dispatchers.IO)
    fun insertItem(cartItem: MenuInfo?) {
        try {
            scope.launch {
                withContext(CoroutineUtils.ioThread) {
                    val item = TummocRoomDatabase.getDataBase()?.cartDao()
                        ?.getItemByMenuId(cartItem?.id ?: 0)
                    if (item == null) {
                        val userCart = UserCart().apply {
                            id = cartItem?.id
                            name = cartItem?.name
                            price = cartItem?.price
                            icon = cartItem?.icon
                        }
                        TummocRoomDatabase.getDataBase()?.cartDao()?.insert(userCart)
                    } else {
                        TummocRoomDatabase.getDataBase()?.cartDao()
                            ?.updateQuantity(item.id ?: 0, item.quantity.plus(1))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "insetUser: " + e.printStackTrace())
        }
    }

    fun itemPlus(cartItem: MenuInfo?) {
        try {
            scope.launch {
                withContext(CoroutineUtils.ioThread) {
                    val item = TummocRoomDatabase.getDataBase()?.cartDao()
                        ?.getItemByMenuId(cartItem?.id ?: 0)
                    if (item != null) {
                        TummocRoomDatabase.getDataBase()?.cartDao()
                            ?.updateQuantity(item.id ?: 0, item.quantity.plus(1))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "insetUser: " + e.printStackTrace())
        }
    }

    fun itemRemove(cartItem: MenuInfo?) {
        try {
            scope.launch {
                withContext(CoroutineUtils.ioThread) {
                    val item = TummocRoomDatabase.getDataBase()?.cartDao()
                        ?.getItemByMenuId(cartItem?.id ?: 0)
                    if (item != null) {
                        if (item.quantity == 1) {
                            TummocRoomDatabase.getDataBase()?.cartDao()
                                ?.deleteItem(item.id ?: 0)
                        } else {
                            TummocRoomDatabase.getDataBase()?.cartDao()
                                ?.updateQuantity(item.id ?: 0, item.quantity.minus(1))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "insetUser: " + e.printStackTrace())
        }
    }

    private var _allCartItem: MutableLiveData<List<UserCart>>? = MutableLiveData()
    fun observeCartItem() = _allCartItem

    fun getAllCartItem() {
        try {
            scope.launch {
                withContext(CoroutineUtils.ioThread) {
                    _allCartItem?.postValue(
                        TummocRoomDatabase.getDataBase()?.cartDao()?.getAllItem()
                    )
                }
            }
        } catch (e: Exception) {
            _allCartItem?.postValue(null)
            Log.e("TAG", "insetUser: " + e.printStackTrace())
        }
    }
}


