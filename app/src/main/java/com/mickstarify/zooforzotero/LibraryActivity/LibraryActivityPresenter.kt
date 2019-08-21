package com.mickstarify.zooforzotero.LibraryActivity

import android.content.Context
import android.util.Log
import com.mickstarify.zooforzotero.ZoteroAPI.Model.Collection
import com.mickstarify.zooforzotero.ZoteroAPI.Model.Item
import java.io.File

class LibraryActivityPresenter(val view : Contract.View, context : Context) : Contract.Presenter {
    override fun openPDF(attachment: File) {
        view.hideDownloadProgress()
        view.openPDF(attachment)
    }

    override fun openAttachment(item: Item) {
        view.showDownloadProgress()
        model.openAttachment(item)
    }

    override fun stopLoading() {
        if (!model.loadingCollections && !model.loadingItems) {
            view.hideLoadingAnimation()
        }
    }

    override fun requestLibraryRefresh() {
        view.showLoadingAnimation()
        model.refreshLibrary()
    }

    override fun selectItem(item: Item) {
        view.showItemDialog(item, model.getAttachments(item.ItemKey))
    }

    override fun setCollection(collectionName: String) {
        Log.d("zotero", "Got request to change collection to ${collectionName}")
        if (collectionName == "all"){
            view.setTitle("My Library")
            view.populateItems(model.getLibraryItems().sortedBy { it.getTitle().toLowerCase() })
        }
        else{
            view.setTitle(collectionName)
            view.populateItems(model.getItemsFromCollection(collectionName).sortedBy { it.getTitle().toLowerCase() })
        }
    }

    override fun testConnection() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun receiveCollections(collections: List<Collection>) {
        for (collection : Collection in collections.filter {
            !it.hasParent()
        }.sortedBy { it.getName().toLowerCase() }){
            Log.d("zotero", "Got collection ${collection.getName()}")
            view.addNavigationEntry(collection, "Catalog")
        }
    }

    override fun createErrorAlert(title: String, message: String) {
        view.createErrorAlert(title, message)
    }

    override fun getItemEntries(): List<Contract.View> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val model = LibraryActivityModel(this, context)

    init {
        view.initUI()
        view.showLoadingAnimation()
        model.requestCollections({receiveCollections(model.getCollections()!!)})
        model.requestItems({this.setCollection("all")})
    }
}