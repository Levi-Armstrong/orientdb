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
package com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations;

import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OLogSequenceNumber;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OOperationUnitId;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OWriteAheadLog;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Note: all atomic operations methods are designed in context that all operations on single files will be wrapped in shared lock.
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 12/3/13
 */
public final class OAtomicOperation {

  private final OLogSequenceNumber startLSN;
  private final OOperationUnitId   operationUnitId;

  private int     startCounter;
  private boolean rollback;

  private final Set<String> lockedObjects = new HashSet<>();


  private final Map<String, OAtomicOperationMetadata<?>> metadata = new LinkedHashMap<>();

  public OAtomicOperation(final OLogSequenceNumber startLSN, final OOperationUnitId operationUnitId) {
    this.startLSN = startLSN;
    this.operationUnitId = operationUnitId;

    startCounter = 1;
  }

  public OOperationUnitId getOperationUnitId() {
    return operationUnitId;
  }

  /**
   * Add metadata with given key inside of atomic operation. If metadata with the same key insist inside of atomic operation it will
   * be overwritten.
   *
   * @param metadata Metadata to add.
   *
   * @see OAtomicOperationMetadata
   */
  public void addMetadata(final OAtomicOperationMetadata<?> metadata) {
    this.metadata.put(metadata.getKey(), metadata);
  }

  /**
   * @param key Key of metadata which is looking for.
   *
   * @return Metadata by associated key or <code>null</code> if such metadata is absent.
   */
  public OAtomicOperationMetadata<?> getMetadata(final String key) {
    return metadata.get(key);
  }

  /**
   * @return All keys and associated metadata contained inside of atomic operation
   */
  private Map<String, OAtomicOperationMetadata<?>> getMetadata() {
    return Collections.unmodifiableMap(metadata);
  }

  OLogSequenceNumber finalizeTx(final OWriteAheadLog writeAheadLog) throws IOException {
    return writeAheadLog.logAtomicOperationEndRecord(getOperationUnitId(), rollback, this.startLSN, getMetadata());
  }

  void incrementCounter() {
    startCounter++;
  }

  void decrementCounter() {
    startCounter--;
  }

  public int getCounter() {
    return startCounter;
  }

  void rollback() {
    rollback = true;
  }

  boolean isRollback() {
    return rollback;
  }

  void addLockedObject(final String lockedObject) {
    lockedObjects.add(lockedObject);
  }

  boolean containsInLockedObjects(final String objectToLock) {
    return lockedObjects.contains(objectToLock);
  }

  Iterable<String> lockedObjects() {
    return lockedObjects;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final OAtomicOperation operation = (OAtomicOperation) o;

    return operationUnitId.equals(operation.operationUnitId);
  }

  @Override
  public int hashCode() {
    return operationUnitId.hashCode();
  }
}
