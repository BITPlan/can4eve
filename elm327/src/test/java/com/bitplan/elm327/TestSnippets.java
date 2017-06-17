package com.bitplan.elm327;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSnippets {
   
  @Test
  public void testSnippetComplete() {
    String snippets[] = { "A", "OK>" };
    boolean expected[] = { true, false };
    int index = 0;
    ConnectionImpl con=new ConnectionImpl();
    for (String snippet : snippets) {
      con.addSnippet(snippet);
      Packet p=con.getResponse(null);
      assertNotNull(p);
      assertEquals(snippets[index],expected[index], p.isTimeOut());
      if (!p.isTimeOut()) {
        System.out.println(p.getData());
      }
      index++;
    }
  }
}
