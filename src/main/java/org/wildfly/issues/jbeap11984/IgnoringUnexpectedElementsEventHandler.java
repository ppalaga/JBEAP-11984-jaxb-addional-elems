package org.wildfly.issues.jbeap11984;
import java.util.Locale;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class IgnoringUnexpectedElementsEventHandler implements ValidationEventHandler {

  @Override
  public boolean handleEvent(ValidationEvent event) {
      if (event.getMessage().toLowerCase(Locale.US).contains("unexpected element")) {
          /* ignore */
          return true;
      } else {
          return false;
      }
  }
}