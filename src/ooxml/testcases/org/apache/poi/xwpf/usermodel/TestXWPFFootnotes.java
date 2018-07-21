/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.poi.xwpf.XWPFTestDataSamples;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFtnEdn;

import junit.framework.TestCase;

public class TestXWPFFootnotes extends TestCase {
    
    public void testCreateFootnotes() throws IOException{
        XWPFDocument docOut = new XWPFDocument();

        XWPFFootnotes footnotes = docOut.createFootnotes();
        
        assertNotNull(footnotes);
        
        XWPFFootnotes secondFootnotes = docOut.createFootnotes();
        
        assertSame(footnotes, secondFootnotes);
        
        docOut.close();
    }

    public void testAddFootnotesToDocument() throws IOException {
        XWPFDocument docOut = new XWPFDocument();

        // NOTE: XWPFDocument.createFootnote() delegates directly
        //       to XWPFFootnotes.createFootnote() so this tests
        //       both creation of new XWPFFootnotes in document
        //       and XWPFFootnotes.createFootnote();
        XWPFFootnote footnote = docOut.createFootnote();
        BigInteger noteId = footnote.getId();

        XWPFDocument docIn = XWPFTestDataSamples.writeOutAndReadBack(docOut);

        XWPFFootnote note = docIn.getFootnoteByID(noteId.intValue());
        assertNotNull(note);
        assertEquals(STFtnEdn.NORMAL, note.getCTFtnEdn().getType());
    }

    /**
     * Bug 55066 - avoid double loading the footnotes
     */
    public void testLoadFootnotesOnce() throws IOException {
        XWPFDocument doc = XWPFTestDataSamples.openSampleDocument("Bug54849.docx");
        List<XWPFFootnote> footnotes = doc.getFootnotes();
        int hits = 0;
        for (XWPFFootnote fn : footnotes) {
            for (IBodyElement e : fn.getBodyElements()) {
                if (e instanceof XWPFParagraph) {
                    String txt = ((XWPFParagraph) e).getText();
                    if (txt.contains("Footnote_sdt")) {
                        hits++;
                    }
                }
            }
        }
        assertEquals("Load footnotes once", 1, hits);
    }
}

