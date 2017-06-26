/**
 *
 * This file is part of the https://github.com/BITPlan/can4eve open source project
 *
 * Copyright 2017 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.can4eve.gui.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
/**
 * see License free version of
 * https://bitbucket.org/dimo414/jgrep/src/8d8e21bbc20f3e88490631e8603d93b5d3c5d2d8/src/grep/JLink.java?at=default&fileviewer=file-view-default
 
 * A JLabel that behaves like a hyperlink, launching the default browser when clicked.
 * 
 * The link is styled like a standard &lt;a&gt; tag in a browser; blue and underlined
 * by default, and changing color as the user interacts with it.
 */
// http://stackoverflow.com/q/527719/113632
public class JLink extends JLabel {
  private static final long serialVersionUID = 8273875024682878518L;

  private volatile String text;
  private volatile URI uri;
  private volatile LinkStyle inactive;

  /**
   * Constructs a JLink with the given text that will launch the given URI when clicked.
   * 
   * @throws IllegalArgumentException if uri is not a valid URI
   */
  public JLink(String text, String uri) {
    super(text);
    if (uri==null)
      uri="";
    init(URI.create(uri));
  }

  /**
   * Constructs a JLink with the given text that will launch the given URI when clicked.
   */
  public JLink(String text, URI uri) {
    super(text);
    init(uri);
   }
  
  /**
   * initialize me with the given uri
   * @param uri
   */
  public void init(URI uri) {
    setLink(uri);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        open(JLink.this.uri);
        inactive = LinkStyle.VISITED;
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateText(LinkStyle.ACTIVE);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        updateText(inactive);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
    });
  }

  /**
   * Updates the linked URI, and resets the link style to unvisited.
   */
  public void setLink(URI uri) {
    if (uri == null) {
      throw new NullPointerException();
    }
    this.uri = uri;
    setToolTipText(uri.toString());
    inactive = LinkStyle.UNVISITED;
    updateText(inactive);
  }

  /**
   * Updates the linked URI, and resets the link style to unvisited.
   *
   * @throws IllegalArgumentException if uri is not a valid URI
   */
  public void setLink(String uri) {
    setLink(URI.create(uri));
  }

  /**
   * Styles the text like a link, in addition to the default behavior.
   * 
   * {@inheritDoc}
   */
  @Override
  public void setText(String text) {
    if (text == null) {
      throw new NullPointerException();
    }
    this.text = text;
    // inactive is still null when called from JLabel's constructor
    updateText(inactive == null ? LinkStyle.UNVISITED : inactive);
    
  }

  private void updateText(LinkStyle style) {
    super.setText(style.format(text));
  }
  
  public URI getLink() {
    return uri;
  }

  public String getLinkText() {
    return text;
  }

  /**
   * open the given url
   * @param url
   */
  public static void open(String url) {
    URI luri = URI.create(url);
    open(luri);
  }
  /**
   * Attempts to open a URI in the user's default browser, displaying a graphical warning message
   * if it fails.
   */
  public static void open(URI uri) {
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      try {
        desktop.browse(uri);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null,
            "Failed to open '" + uri + "' - your computer is likely misconfigured.",
            "Cannot Open Link", JOptionPane.WARNING_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(null, "Java is not able to open a browser on your computer.",
          "Cannot Open Link", JOptionPane.WARNING_MESSAGE);
    }
  }
  
  private enum LinkStyle {
    UNVISITED(new Color(0x00, 0x00, 0x99), true),
    ACTIVE(new Color(0x99, 0x00, 0x00), false),
    VISITED(new Color(0x80, 0x00, 0x80), true);
    
    private static final String FORMAT_STRING =
        "<html><span style=\"color: #%02X%02X%02X;\">%s</span></html>";
    
    private final Color color;
    private final boolean underline;
    
    LinkStyle(Color c, boolean u) {
      color = c;
      underline = u;
    }

    public String format(String text) {
      String underlinedText = underline ? "<u>" + text + "</u>" : text;
      return String.format(
          FORMAT_STRING, color.getRed(), color.getGreen(), color.getBlue(), underlinedText);
    }
  }

}
