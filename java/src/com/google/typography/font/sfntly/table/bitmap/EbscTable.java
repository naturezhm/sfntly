/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.typography.font.sfntly.table.bitmap;

import com.google.typography.font.sfntly.data.ReadableFontData;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.Header;
import com.google.typography.font.sfntly.table.SubTable;
import com.google.typography.font.sfntly.table.Table;

/**
 * @author Stuart Gill
 *
 */
public class EbscTable extends Table {

  private interface HeaderOffset {
    int version = 0;
    int numSizes = 4;
    int SIZE = 8;
  }

  private interface BitmapScale {
    int hori = 0;
    int vert = 12;
    int ppemX = 24;
    int ppemY = 25;
    int substitutePpemX = 26;
    int substitutePpemY = 27;
    int SIZE = 28;
  }

  /**
   * @param header
   * @param data
   */
  private EbscTable(Header header, ReadableFontData data) {
    super(header, data);
  }

  public int version() {
    return this.data.readFixed(HeaderOffset.version);
  }

  public int numSizes() {
    return this.data.readULongAsInt(HeaderOffset.numSizes);
  }

  public BitmapScaleTable bitmapScaleTable(int index) {
    if (index < 0 || index > this.numSizes() - 1) {
      throw new IndexOutOfBoundsException(
          "BitmapScaleTable index is outside the bounds of available tables.");
    }
    return new BitmapScaleTable(this.data, HeaderOffset.SIZE + index * BitmapScale.SIZE);
  }

  public static class BitmapScaleTable extends SubTable {
    protected BitmapScaleTable(ReadableFontData data, int offset) {
      super(data, offset, BitmapScale.SIZE);
    }

    public SbitLineMetrics hori() {
      ReadableFontData horiData = this.data.slice(BitmapScale.hori, SbitLineMetrics.SIZE);
      return new SbitLineMetrics(horiData, this.data);
    }

    public SbitLineMetrics vert() {
      ReadableFontData horiData = this.data.slice(BitmapScale.vert, SbitLineMetrics.SIZE);
      return new SbitLineMetrics(horiData, this.data);
    }

    public int ppemX() {
      return this.data.readByte(BitmapScale.ppemX);
    }

    public int ppemY() {
      return this.data.readByte(BitmapScale.ppemY);
    }

    public int substitutePpemX() {
      return this.data.readByte(BitmapScale.substitutePpemX);
    }

    public int substitutePpemY() {
      return this.data.readByte(BitmapScale.substitutePpemY);
    }
  }

  // TODO(stuartg): currently the builder just builds from initial data
  // - need to make fully working but few if any examples to test with
  public static class Builder extends Table.Builder<EbscTable> {
    /**
     * Create a new builder using the header information and data provided.
     *
     * @param header the header information
     * @param data the data holding the table
     * @return a new builder
     */
    public static Builder createBuilder(Header header, WritableFontData data) {
      return new Builder(header, data);
    }

    /**
     * @param header
     * @param data
     */
    protected Builder(Header header, WritableFontData data) {
      super(header, data);
    }

    /**
     * @param header
     * @param data
     */
    protected Builder(Header header, ReadableFontData data) {
      super(header, data);
    }

    @Override
    protected EbscTable subBuildTable(ReadableFontData data) {
      return new EbscTable(this.header(), data);
    }

    @Override
    protected void subDataSet() {
      // NOP
    }

    @Override
    protected int subDataSizeToSerialize() {
      return 0;
    }

    @Override
    protected boolean subReadyToSerialize() {
      return false;
    }

    @Override
    protected int subSerialize(WritableFontData newData) {
      return 0;
    }

  }
}
