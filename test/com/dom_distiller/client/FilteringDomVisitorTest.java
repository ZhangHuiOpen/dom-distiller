// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.junit.client.GWTTestCase;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.StyleElement;

public class FilteringDomVisitorTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    private void runTest(String innerHtml, String expectedHtml) throws Throwable {
        Element container = Document.get().createDivElement();
        container.setInnerHTML(innerHtml);
        SimpleContentHandler contentHandler = new SimpleContentHandler();
        DomWalker.Visitor domVisitor = new DomToSaxVisitor(contentHandler);
        FilteringDomVisitor filteringDomVisitor = new FilteringDomVisitor(domVisitor);
        new DomWalker(filteringDomVisitor).walk(container);
        String expectedDocument = "<div>" + expectedHtml + "</div>";
        assertEquals(expectedDocument, contentHandler.getDocumentString().toLowerCase());
    }

    public void testVisibleText() throws Throwable {
        String html = "visible text";
        runTest(html, html);
    }

    public void testVisibleElement() throws Throwable {
        String html = "<div>visible element</div>";
        runTest(html, html);
    }

    public void testDisplayNone() throws Throwable {
        runTest("<div style=\"display:none\">diplay none</div>", "");
    }

    public void testVisibilityHidden() throws Throwable {
        runTest("<div style=\"visibility:hidden\">visibility hidden</div>", "");
    }

    public void testInvisibleInVisible() throws Throwable {
        String html = "<div>visible parent" +
                          "<div style=\"display:none\">invisible child</div>" +
                      "</div>";
        runTest(html, "<div>visible parent</div>");
    }

    public void testVisibleInInvisible() throws Throwable {
        String html = "<div style=\"visibility:hidden\">invisible parent" +
                          "<div>visible child</div>" +
                      "</div>";
        runTest(html, "");
    }

    public void testVisibleInVisible() throws Throwable {
        String html = "<div>visible parent" +
                          "<div>visible child</div>" +
                      "</div>";
        runTest(html, html);
    }

    public void testInvisibleInInvisible() throws Throwable {
        String html = "<div style=\"visibility:hidden\">invisible parent" +
                          "<div style=\"display:none\">invisible child</div>" +
                      "</div>";
        runTest(html, "");
    }

    public void testDifferentChildrenInVisible() throws Throwable {
        String html = "<div>visible parent" +
                          "<div style=\"display:none\">invisible child0</div>" +
                          "<div>visible child1" +
                              "<div style=\"visibility:hidden\">invisible grandchild</div>" +
                          "</div>" +
                          "<div style=\"visibility:hidden\">invisible child2</div>" +
                      "</div>";
        runTest(html, "<div>visible parent<div>visible child1</div></div>");
    }

    public void testDifferentChildrenInInvisible() throws Throwable {
        String html = "<div style=\"visibility:hidden\">invisible parent" +
                          "<div style=\"display:none\">invisible child0</div>" +
                          "<div>visible child1" +
                              "<div style=\"display:none\">invisible grandchild</div>" +
                          "</div>" +
                          "<div style=\"visibility:hidden\">invisible child2</div>" +
                      "</div>";
        runTest(html, "");
    }

    // Note: getComputedStyle() doesn't work correctly when running ant test.dev or test.prod,
    // but it works in production mode in the browser, so the test is in chrome's components/
    // dom_distiller/content/distiller_page_web_contents_browsertest.cc::VisibilityDetection().

    public void testDataTable() throws Throwable {
        String html = "<table align=\"left\">" +
                          // <caption> tag makes this a data table.
                          "<caption>Testing Data Table</caption>" +
                          "<tbody align=\"left\">" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                              "</tr>" +
                          "</tbody>" +
                      "</table>";
        runTest(html, "");
    }

    public void testNonDataTable() throws Throwable {
        String html = "<table align=\"left\">" +
                          "<tbody align=\"left\">" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                              "</tr>" +
                          "</tbody>" +
                      "</table>";
        runTest(html, html);
    }
}
