package com.github.stippi;

import com.github.stippi.model.Document;
import com.github.stippi.ui.MainWindow;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class App {
    public static void main( String[] args) {
        Display display = Display.getDefault();

        Document document = new Document();
        MainWindow window = new MainWindow(document);
        Shell shell = window.open();

        while (!shell.isDisposed()) {
            display.readAndDispatch();
        }
    }
}
