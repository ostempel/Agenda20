package edu.hm.cs.katz.swt2.agenda.initialization;

import edu.hm.cs.katz.swt2.agenda.common.SecurityHelper;
import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.UserService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Initialisierung von Demo-Daten. Diese Komponente erstellt beim Systemstart Anwender, Topics,
 * Abonnements usw., damit man die Anwendung mit allen Features vorführen kann.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
@Profile("demo")
public class DemoData {

  private static final String LOGIN_FINE = "fine";
  private static final String LOGIN_ERNIE = "ernie";
  private static final String LOGIN_BERT = "bert";
  private static final String LOGIN_MAXI = "maxi";
  private static final String LOGIN_LUCAS = "lucas";

  private static final String LOGIN_TIMO = "timo";
  private static final String LOGIN_TETE = "tete";
  private static final String LOGIN_ANNA = "anna";

  private static final Logger LOG = LoggerFactory.getLogger(DemoData.class);

  private static final String DUE_DATE1 = "30.09.2020";

  private static final String DUE_DATE2 = "24.07.2020";

  private static final String DUE_DATE3 = "12.01.2021";

  @Autowired
  UserService anwenderService;

  @Autowired
  TopicService topicService;

  @Autowired
  TaskService taskService;

  /**
   * Erstellt die Demo-Daten.
   */
  @PostConstruct
  @SuppressWarnings("unused")
  public void addData() {
    SecurityHelper.escalate(); // admin rights
    LOG.debug("Erzeuge Demo-Daten.");

    anwenderService.legeAn(LOGIN_FINE, "Fine", "FineFine1.", false, "fine.ProjetAgenda20SOS@gmail.com");
    anwenderService.legeAn(LOGIN_ERNIE, "Ernie", "ErnieErnie1.", false, "ernie.ProjetAgenda20SOS@gmail.com");
    anwenderService.legeAn(LOGIN_BERT, "Bert", "BertBert1.", false, "bert.ProjetAgenda20SOS@gmail.com");
    anwenderService.legeAn(LOGIN_MAXI, "Maxi", "MaxiMaxi1.", false, "maxi.ProjetAgenda20SOS@gmail.com");
    anwenderService.legeAn(LOGIN_LUCAS, "Lucas", "LucasLucas1.", false, "lucas.ProjetAgenda20SOS@gmail.com");

    anwenderService.registerNewUser(LOGIN_ANNA, "Anna", "AnnaAnna1.", "AnnaAnna1.",
        "anna.ProjetAgenda20SOS@gmail.com");
    anwenderService.registerNewUser(LOGIN_TETE, "Tete", "TeteTete1.", "TeteTete1.",
        "tete.ProjetAgenda20SOS@gmail.com");
    anwenderService.registerNewUser(LOGIN_TIMO, "Timo", "TimoTimo1.", "TimoTimo1.",
        "timo.ProjetAgenda20SOS@gmail.com");


    String htmlKursUuid = topicService.createTopic("HTML für Anfänger", "HTML-Kurs für Anfänger",
        "Dies ist ein HTML-Kurs auf Anfänger-Niveau.", LOGIN_FINE);
    topicService.subscribe(htmlKursUuid, LOGIN_ERNIE);
    topicService.subscribe(htmlKursUuid, LOGIN_BERT);
    Long linkErstellenTask = taskService.createTask(htmlKursUuid, "Link erstellen",
        "Lerne wie man Links in HTML erstellt!",
        "Hier wird dir beigebracht wie man Links in HTML erstellt mit all seinen Attributen.",
        LOGIN_FINE, DUE_DATE1, false);
    Long leeresHtmlTemplate = taskService.createTask(htmlKursUuid, "Leeres HTML-Template erstellen",
        "Erstelle die Basis für ein HTML-Dokument!",
        "Ein leeres HTML-Dokument kann als Template für zukünftige Projekte dienen!", LOGIN_FINE,
        DUE_DATE2, false);
    Long bodyUndHeaderAnleitung = taskService.createTask(htmlKursUuid,
        "Body- und Header-Anleitgung", "Hier lernst du Body und Header von HTML kennen!",
        "Innerhalb des Header werden Metadaten des Dokuments gesammelt und innerhalb des Body werden die tatsächlichen Websitedaten eingefügt.",
        LOGIN_FINE, DUE_DATE1, false);
    Long bilderEinbetten = taskService.createTask(htmlKursUuid, "Bilder einfügen",
        "Hier lernst du wie man Bilder einfügt!",
        "Um deine Website schöner zu gestallten kann man Bilder einbinden.", LOGIN_FINE, DUE_DATE3,
        false);
    taskService.checkTask(linkErstellenTask, LOGIN_ERNIE);
    taskService.checkTask(bodyUndHeaderAnleitung, LOGIN_ERNIE);
    taskService.checkTask(bodyUndHeaderAnleitung, LOGIN_BERT);
    taskService.checkTask(bilderEinbetten, LOGIN_ERNIE);
    taskService.checkTask(leeresHtmlTemplate, LOGIN_BERT);

    String cssKursUuid =
        topicService.createTopic("CSS für Fortgeschrittene", "CSS-Kurs für Fortgeschrittene",
            "Dies ist ein CSS-Kurs auf Fortgeschrittenen-Niveau.", LOGIN_FINE);
    topicService.subscribe(cssKursUuid, LOGIN_ERNIE);
    Long zentrierenEinesTextes = taskService.createTask(cssKursUuid, "Zentrieren eines Textes",
        "Zentriere einen Text auf der Website!",
        "Hier lernst du wie man mithilfe von CSS Text zentrieren kann.", LOGIN_FINE, DUE_DATE1,
        false);
    Long schriftartAendern = taskService.createTask(cssKursUuid, "Schriftart ändern",
        "Ändere Schriftarten von allen Texten!",
        "Die normale Schriftart wirkt oft langweilig... Deswegen wollen wir diese ändern.",
        LOGIN_FINE, DUE_DATE1, false);
    Long hintergrundbildVerwenden = taskService.createTask(cssKursUuid, "Hintergrundbild verwenden",
        "Gib deiner Website ein Hintergrundbild!",
        "Verwende ein Hintergrundbild, da ein weißer Hintergrund sehr langweilig ist.", LOGIN_FINE,
        DUE_DATE1, false);
    Long klassenUndIdsAnleitung = taskService.createTask(cssKursUuid, "Klassen und IDs Anleitung",
        "Benutze Klassen und IDs!",
        "Mithilfe von Klassen und IDs kannst du auf Elemente deiner HTML-Seite zugreifen und verändern",
        LOGIN_FINE, DUE_DATE1, false);
    Long externeStylesheetBenutzen =
        taskService.createTask(cssKursUuid, "Externes Stylesheet benutzen", "Trenne CSS und HTML!",
            "Hier lernst du wie man ein externes Stylesheet verwenden kann!", LOGIN_FINE, DUE_DATE1,
            false);
    taskService.checkTask(zentrierenEinesTextes, LOGIN_ERNIE);
    taskService.checkTask(hintergrundbildVerwenden, LOGIN_ERNIE);
    taskService.checkTask(klassenUndIdsAnleitung, LOGIN_ERNIE);

    String javaScriptKursUuid = topicService.createTopic("JavaScript für Anfänger",
        "JavaScript für Anfänger", "Dies ist ein JavaScript-Kurs auf Anfänger-Niveau.", LOGIN_FINE);
    topicService.subscribe(javaScriptKursUuid, LOGIN_ERNIE);
    Long javaScriptEinstieg = taskService.createTask(javaScriptKursUuid, "JavaScript Einstieg",
        "Bau dynamische Websiten!",
        "Mithilfe von JavaScript kann man Seiten clientseitig dynamisch gestalten!", LOGIN_FINE,
        DUE_DATE2, false);
    Long syntaxtVonJavaScript =
        taskService.createTask(javaScriptKursUuid, "Syntaxt von JavaScript", "Lerne den Syntax!",
            "Hier lernst du wie der Syntax von JavaScript ist!", LOGIN_FINE, DUE_DATE3, false);
    Long variablenDefenieren = taskService.createTask(javaScriptKursUuid, "Variablen Definieren",
        "Lerne Variablen kennen!",
        "Mithilfe von Variablen kannst du Inhalte abspeichern und wiederverwenden.", LOGIN_FINE,
        DUE_DATE3, false);
    Long helloWorldProgramm = taskService.createTask(javaScriptKursUuid, "HelloWorld-Programm",
        "Dein erste Programm!", "Erstelle dein erstes Programm.", LOGIN_FINE, DUE_DATE1, false);
    taskService.checkTask(javaScriptEinstieg, LOGIN_ERNIE);
    taskService.checkTask(variablenDefenieren, LOGIN_ERNIE);

    String sqlUuid = topicService.createTopic("SQL Einstiegskurs", "SQL Einstiegskurs",
        "Dies ist ein SQL-Kurs auf Anfänger-Niveau.", LOGIN_FINE);
    topicService.subscribe(sqlUuid, LOGIN_ERNIE);

    String erniesBackKursUuid =
        topicService.createTopic("Ernies Backkurs", "Ernies Backkurs für Anfänger",
            "Dies ist ein Backkurs auf Anfänger-Niveau. Veranstalter ist Ernie.", LOGIN_ERNIE);
    topicService.subscribe(erniesBackKursUuid, LOGIN_BERT);
    topicService.subscribe(erniesBackKursUuid, LOGIN_FINE);
    Long googlehupfBacken =
        taskService.createTask(erniesBackKursUuid, "Googlehupf backen", "Wir backen zusammen!",
            "Hier lernst du Tipps und Tricks für das Googlehupf-Backen...", LOGIN_ERNIE, DUE_DATE1, false);
    Long affenMuffinTask = taskService.createTask(erniesBackKursUuid, "Affenmuffins backen",
        "Wir backen zusammen!", "Hier lernst du Tipps und Tricks für das Affenmuffins-Backen...",
        LOGIN_ERNIE, DUE_DATE2, false);
    Long mamorkuchenBacken = taskService.createTask(erniesBackKursUuid, "Mamorkuchen backen",
        "Wir backen zusammen!", "Hier lernst du Tipps und Tricks für das Mamorkuchen-Backen...",
        LOGIN_ERNIE, DUE_DATE3, false);
    Long plaetzchenBacken =
        taskService.createTask(erniesBackKursUuid, "Plätzchen backen", "Wir backen zusammen!",
            "Hier lernst du Tipps und Tricks für das Plätzchen-Backen...", LOGIN_ERNIE, DUE_DATE1, false);
    Long brezenBacken =
        taskService.createTask(erniesBackKursUuid, "Brezen backen", "Wir backen zusammen!",
            "Hier lernst du Tipps und Tricks für das Brezen-Backen...", LOGIN_ERNIE, DUE_DATE1, false);
    taskService.checkTask(affenMuffinTask, LOGIN_BERT);
    taskService.checkTask(brezenBacken, LOGIN_BERT);
    taskService.checkTask(mamorkuchenBacken, LOGIN_BERT);
    taskService.commentTask(mamorkuchenBacken, LOGIN_BERT, "desch is 1 komentahr");

    String erniesBastelKursUuid =
        topicService.createTopic("Ernies Bastelkurs", "Ernies Bastelkurs für Anfänger",
            "Dies ist ein Bastelkurs auf Anfänger-Niveau. Veranstalter ist Ernie.", LOGIN_ERNIE);
    topicService.subscribe(erniesBastelKursUuid, LOGIN_BERT);
    topicService.subscribe(erniesBastelKursUuid, LOGIN_FINE);
  }
}
