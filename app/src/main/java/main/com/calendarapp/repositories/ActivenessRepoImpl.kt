package main.com.calendarapp.repositories

import io.objectbox.Box
import io.objectbox.rx.RxQuery
import main.com.calendarapp.data.ObjectBox
import main.com.calendarapp.models.Activeness
import main.com.calendarapp.models.Activeness_

class ActivenessRepoImpl : ActivenessRepo {


    private var activenessBox: Box<Activeness> = ObjectBox.boxStore.boxFor(Activeness::class.java)

    override fun getAllActivenesses() = RxQuery.observable( activenessBox.query().build())!!

    override fun getActivenessById(id: Long) = RxQuery.observable( activenessBox.query().equal(Activeness_.id, id).build())!!

    override fun saveActiveness(activeness: Activeness) {
        activenessBox.put(activeness)
    }

    override fun deleteAll() {
        activenessBox.removeAll()
    }

    override fun delete(activeness: Activeness) {
        activenessBox.remove(activeness)
    }

}

