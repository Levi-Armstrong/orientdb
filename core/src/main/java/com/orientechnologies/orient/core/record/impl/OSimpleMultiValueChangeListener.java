/*
 *
 *  *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 *
 */

package com.orientechnologies.orient.core.record.impl;

import com.orientechnologies.orient.core.db.record.OMultiValueChangeEvent;
import com.orientechnologies.orient.core.db.record.OMultiValueChangeListener;
import com.orientechnologies.orient.core.db.record.OMultiValueChangeTimeLine;
import com.orientechnologies.orient.core.db.record.ORecordElement.STATUS;

import java.lang.ref.WeakReference;

/**
 * Perform gathering of all operations performed on tracked collection and create mapping between list of collection operations and
 * field name that contains collection that was changed.
 *
 * @param <K> Value that uniquely identifies position of item in collection
 * @param <V> Item value.
 */
final class OSimpleMultiValueChangeListener<K, V> implements OMultiValueChangeListener<K, V> {
  /**
   *
   */
  private final WeakReference<ODocument> oDocument;
  private final ODocumentEntry           entry;

  OSimpleMultiValueChangeListener(ODocument oDocument, final ODocumentEntry entry) {
    this.oDocument = new WeakReference<ODocument>(oDocument);
    this.entry = entry;
  }

  public void onAfterRecordChanged(final OMultiValueChangeEvent<K, V> event) {
    ODocument document = oDocument.get();
    if (document == null)
      //doc not alive anymore, do nothing.
      return;
    if (document.getInternalStatus() != STATUS.UNMARSHALLING) {
      if (event.isChangesOwnerContent())
        document.setDirty();
      else
        document.setDirtyNoChanged();
    }

    if (!(document.trackingChanges && document.getIdentity().isValid()) || document.getInternalStatus() == STATUS.UNMARSHALLING)
      return;

    if (entry == null || entry.isChanged())
      return;

    if (entry.timeLine == null) {
      entry.timeLine = new OMultiValueChangeTimeLine<Object, Object>();
    }

    entry.timeLine.addCollectionChangeEvent((OMultiValueChangeEvent<Object, Object>) event);
  }
}
