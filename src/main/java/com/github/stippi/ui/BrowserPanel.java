package com.github.stippi.ui;

import com.github.stippi.model.Document;
import com.github.stippi.model.DocumentListener;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class BrowserPanel extends Composite {

    private final Document  fDocument;
    private final Browser   fBrowser;

    public BrowserPanel(Composite parent, Document document) {
        super(parent, SWT.NULL);
        fDocument = document;
        final DOMPropertyUpdater domUpdater = new DOMPropertyUpdater();
        fDocument.addListener(domUpdater);
        addDisposeListener(disposeEvent -> fDocument.removeListener(domUpdater));

        fBrowser = new Browser(this, SWT.NULL);
        fBrowser.addLocationListener(new LocationAdapter() {
            @Override
            public void changing(LocationEvent locationEvent) {
                if (locationEvent.location.startsWith("dq://focus")) {
                    try {
                        String query = locationEvent.location.split("\\?")[1];
                        Map<String, String> params = splitQuery(query);
                        String focusPanel = params.get("panel");
                        String focusField = params.get("field");
                        if (focusPanel != null && focusField != null) {
                            FocusManager.switchFocus(focusPanel, focusField);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    locationEvent.doit = false;
                }
            }
        });

        loadBrowserContents();

        setLayout(new FillLayout());
    }

    private static Map<String, String> splitQuery(String query) {
        Map<String, String> paramMap = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            paramMap.put(
                    URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8),
                    URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return paramMap;
    }

    private void loadBrowserContents() {
        InputStream resource = getClass().getResourceAsStream("index.html");
        try {
            if (resource != null) {
                String html = new String(resource.readAllBytes());
                fBrowser.setText(html);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }

    private class DOMPropertyUpdater implements DocumentListener {
        @Override
        public void propertyChanged(String name, String value) {
            fBrowser.execute("fieldChanged(`" + name + "`,`" + value + "`);");
        }
    }
}
