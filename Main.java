package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.util.StringConverter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;

public class Main extends Application {

    public String sivuindeksiksi (String teksti, String suunta) {
        String[] yläindeksi = {"0123456789abcdefghijklmnoprstuvwxyzABDEGHIJKLMNOPRTUVW", "⁰¹²³⁴⁵⁶⁷⁸⁹ᵃᵇᶜᵈᵉᶠᵍʰⁱʲᵏˡᵐⁿᵒᵖʳˢᵗᵘᵛʷˣʸᶻᴬᴮᴰᴱᴳᴴᴵᴶᴷᴸᴹᴺᴼᴾᴿᵀᵁⱽᵂ"};
        String[] alaindeksi = {"0123456789aehijklmnoprstuvx", "₀₁₂₃₄₅₆₇₈₉ₐₑₕᵢⱼₖₗₘₙₒₚᵣₛₜᵤᵥₓ"};
        switch (suunta) {
            case "ylä":
                for (int i=0; i<alaindeksi[1].length(); i++) {
                    for (int j=0; j<teksti.length(); j++) {
                        if (teksti.charAt(j)==alaindeksi[1].charAt(i)) teksti = teksti.substring(0, j) + alaindeksi[0].charAt(i) + teksti.substring(j+1);
                    }
                }
                for (int i=0; i<yläindeksi[0].length(); i++) {
                    for (int j=0; j<teksti.length(); j++) {
                        if (teksti.charAt(j)==yläindeksi[0].charAt(i)) teksti = teksti.substring(0, j) + yläindeksi[1].charAt(i) + teksti.substring(j+1);
                    }
                }
                break;
            case "ala":
                for (int i=0; i<yläindeksi[1].length(); i++) {
                    for (int j=0; j<teksti.length(); j++) {
                        if (teksti.charAt(j)==yläindeksi[1].charAt(i)) teksti = teksti.substring(0, j) + yläindeksi[0].charAt(i) + teksti.substring(j+1);
                    }
                }
                for (int i=0; i<alaindeksi[0].length(); i++) {
                    for (int j=0; j<teksti.length(); j++) {
                        if (teksti.charAt(j)==alaindeksi[0].charAt(i)) teksti = teksti.substring(0, j) + alaindeksi[1].charAt(i) + teksti.substring(j+1);
                    }
                }
                break;
        }
        return teksti;
    }

    final ArrayList<Integer> erikoisK = new ArrayList<>();
    final ArrayList<String[]> valittu = new ArrayList<>();
    final ArrayList<ArrayList<ToggleText>> vaihdettavat = new ArrayList<>();
    final ArrayList<ArrayList<ToggleText>> vaihtamat = new ArrayList<>();

    public void luoRuutu (Stage primaryStage) {
        final int[] lausekeK = {1};
        final int ruutuNumero = erikoisK.size();
        erikoisK.add(1);
        valittu.add(new String[] {""});
        VBox näkö1 = new VBox(5);
        HBox näkö = new HBox(15); näkö.setLayoutX(12.5); näkö.setLayoutY(12.5);
        ScrollPane vieritys = new ScrollPane(näkö); vieritys.setPadding(new Insets(10,10,10,10));
        MenuBar valikko = new MenuBar();
        MenuItem vaihtoFunktioidenAvaus = new MenuItem("erikoisvaihtofunktiot");
        vaihtoFunktioidenAvaus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser tiedostoV = new FileChooser();
                tiedostoV.setTitle("Avaa vaihtofunktiot");
                tiedostoV.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data Files", "*.dat"));
                tiedostoV.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
                File tiedosto = tiedostoV.showOpenDialog(primaryStage);
                if (tiedosto!=null) {
                    String a = "";
                    try (InputStream sisään = new BufferedInputStream(Files.newInputStream(tiedosto.toPath(), READ))) {
                        a = new String(sisään.readAllBytes());
                    } catch (IOException x) {
                        System.err.print(x);
                    }
                    Stage ruutu = new Stage();
                    luoRuutu(ruutu);
                    VBox ruudunNäkö = ((VBox) ruutu.getScene().getRoot());
                    String[] a0;
                    VBox näppäinJoukko2 = ((VBox) ((HBox) ((ScrollPane) ruudunNäkö.getChildren().get(ruudunNäkö.getChildren().size() - 1))
                            .getContent()).getChildren().get(2));
                    VBox lausekeJoukko = ((VBox) ((HBox) ((ScrollPane) ruudunNäkö.getChildren().get(ruudunNäkö.getChildren().size() - 1))
                            .getContent()).getChildren().get(0));
                    for (int i=1, n=StringUtils.ordinalIndexOf(a,"§",i); n!=-1; n=StringUtils.ordinalIndexOf(a,"§",++i)) {
                        int nn = StringUtils.ordinalIndexOf(a,"§",i+1);
                        if (nn==-1) a0 = a.substring(n+1).split(":");
                        else a0 = a.substring(n+1,nn).split(":");
                        if (erikoisK.get(erikoisK.size()-1)<=1) näppäinJoukko2.getChildren().add(new Label("Erikoisvaihtofunktiot"));
                        int ordinaali = erikoisK.get(erikoisK.size()-1)-1;
                        näppäinJoukko2.getChildren().add(new HBox(5));
                        Label yhtäMerkki = new Label("="); yhtäMerkki.setFont(Font.font("Times New Roman", 16));
                        vaihdettavat.get(erikoisK.size()-1).add(new ToggleText(a0[0]));
                        vaihdettavat.get(erikoisK.size()-1).get(ordinaali).solid.setFont(Font.font("Times New Roman", 16));
                        vaihtamat.get(erikoisK.size()-1).add(new ToggleText(a0[1]));
                        vaihtamat.get(erikoisK.size()-1).get(ordinaali).solid.setFont(Font.font("Times New Roman", 16));
                        ((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)).getChildren().addAll(
                                new VBox(2.5, new Button("sovella"), new Button("poista")),
                                vaihdettavat.get(erikoisK.size()-1).get(ordinaali).solid, yhtäMerkki, vaihtamat.get(erikoisK.size()-1).get(ordinaali).solid
                        );
                        asetaErikoisPoistaminen(näppäinJoukko2, ordinaali, l -> l+1);
                        HBox parametrit = new HBox();
                        for (int i1=2; i1<a0.length; i1++) {
                            Label l = new Label(" " + a0[i1] + "=");
                            l.setFont(Font.font("Times New Roman", 16));
                            TextField t = new TextField();
                            t.setPrefWidth(25);
                            parametrit.getChildren().addAll(l, t);
                        }
                        ((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)).getChildren().add(parametrit);
                        asetaVaihdonSoveltaminen(valittu.get(erikoisK.size()-1), näppäinJoukko2, lausekeJoukko, l -> 3*l+2,
                                vaihdettavat.get(erikoisK.size()-1), vaihtamat.get(erikoisK.size()-1), ordinaali, parametrit, l -> l+1);
                        asetaJoukonJäseneksi(vaihdettavat.get(erikoisK.size()-1).get(ordinaali),
                                ((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)), 1);
                        asetaJoukonJäseneksi(vaihtamat.get(erikoisK.size()-1).get(ordinaali),
                                ((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)), 3);
                        asetaYhtälönTäysKäännös(((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)).getChildren().get(2),
                                vaihdettavat.get(erikoisK.size()-1), vaihtamat.get(erikoisK.size()-1), ordinaali);
                        erikoisK.set(erikoisK.size()-1, erikoisK.get(erikoisK.size()-1)+1);
                    }
                }
            }
        });
        Menu avaus = new Menu("Avaa...", null, vaihtoFunktioidenAvaus);
        MenuItem vaihtoFunktioidenTallennus = new MenuItem("erikoisvaihtofunktiot");
        Menu tallennus = new Menu("Tallenna...", null, vaihtoFunktioidenTallennus);
        Menu tiedostoValikko = new Menu("Tiedosto", null, avaus, tallennus);
        MenuItem laskuModulus = new MenuItem("1");
        Menu modulaariLasku = new Menu("Laske valittu moduluksella", null); modulaariLasku.setDisable(true);
        Menu muokkausValikko = new Menu("Muokkaa", null, modulaariLasku, laskuModulus);
        MenuItem koristemuuntajanNäyttö = new MenuItem("Näytä koristemuuntaja");
        TextField koristemuuntaja = new TextField();
        koristemuuntaja.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN:
                    koristemuuntaja.setText(sivuindeksiksi(koristemuuntaja.getText(), "ala"));
                    break;
                case UP:
                    koristemuuntaja.setText(sivuindeksiksi(koristemuuntaja.getText(), "ylä"));
                    break;
            }
        });
        VBox koristemuuntajaJoukko = new VBox(5);
        koristemuuntajaJoukko.getChildren().addAll(new Label("Koristemuuntaja"), koristemuuntaja);
        ((Label) koristemuuntajaJoukko.getChildren().get(0)).setPadding(new Insets(0,10,0,10));
        koristemuuntajanNäyttö.setOnAction(event -> {
            if (näkö1.getChildren().contains(koristemuuntajaJoukko)) {
                näkö1.getChildren().remove(koristemuuntajaJoukko);
                koristemuuntajanNäyttö.setText("Näytä koristemuuntaja");
            }
            else {
                näkö1.getChildren().add(1, koristemuuntajaJoukko);
                koristemuuntajaJoukko.requestFocus();
                koristemuuntajanNäyttö.setText("Piilota koristemuuntaja");
            }
        });
        MenuItem lausekeJoukonTasausVasemmalle = new MenuItem("vasemmalle");
        MenuItem lausekeJoukonTasausOikealle = new MenuItem("oikealle");
        Menu lausekeJoukonTasaus = new Menu("lausekeryhmä", null, lausekeJoukonTasausVasemmalle, lausekeJoukonTasausOikealle);
        Menu tasaus = new Menu("Tasaa...", null, lausekeJoukonTasaus);
        Menu näyttöValikko = new Menu("Näyttö", null, koristemuuntajanNäyttö, tasaus);
        valikko.getMenus().addAll(tiedostoValikko, muokkausValikko, näyttöValikko);
        VBox lausekeJoukko = new VBox(5);
        lausekeJoukonTasausVasemmalle.setOnAction(event -> lausekeJoukko.setAlignment(Pos.CENTER_LEFT));
        lausekeJoukonTasausOikealle.setOnAction(event -> lausekeJoukko.setAlignment(Pos.CENTER_RIGHT));
        ((HBox) vieritys.getContent()).widthProperty().addListener((observable, oldValue, newValue) -> {
            switch (lausekeJoukko.getAlignment()) {
                case CENTER_LEFT:
                    vieritys.setHvalue(0);
                    break;
                case CENTER_RIGHT:
                    vieritys.setHvalue(vieritys.getHmax());
                    break;
            }
        });
        ArrayList<Separator> viivat = new ArrayList<>();
        ArrayList<Label> nimikkeet = new ArrayList<>();
        ArrayList<ToggleText> lausekkeet = new ArrayList<>();
        ComboBox toimintaKohta = new ComboBox(FXCollections.observableArrayList()); toimintaKohta.setDisable(true);
        Button lausekeLisäys = new Button("lisää uusi lauseke");
        lausekeJoukko.getChildren().add(lausekeLisäys);
        toimintaKohta.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> valittu.get(ruutuNumero)[0] = newValue.substring(8));
        VBox näppäinJoukko1 = new VBox(15);
        Button sievennys = new Button("sievene"); sievennys.setDisable(true);
        Button laajennus = new Button("laajenna"); laajennus.setDisable(true);
        GridPane vaihtoJoukko = new GridPane(); vaihtoJoukko.setVgap(15); vaihtoJoukko.setHgap(15);
        Button vaihto = new Button("vaihda"); vaihto.setDisable(true);
        Button erikoisLiitys = new Button("liitä erikoisfunktioihin");
        ToggleText vaihdettava = new ToggleText("x");
        vaihdettava.solid.setFont(Font.font("Times New Roman", 16));
        vaihdettava.textadapt = event -> vaihdettava.solid.setText(lausekkeeksi(vaihdettava.edit.getText()).tekstiksi());
        ToggleText vaihtama = new ToggleText("y");
        vaihtama.solid.setFont(Font.font("Times New Roman", 16));
        vaihtama.textadapt = event -> vaihtama.solid.setText(lausekkeeksi(vaihtama.edit.getText()).tekstiksi());
        HBox vaihtamaJoukko = new HBox(0);
        vaihtamaJoukko.getChildren().addAll(vaihtama.solid, new Label(":lla"));
        vaihtamaJoukko.getChildren().get(1).setOnMouseClicked(vaihtama.solid.getOnMouseClicked());
        ((Label) vaihtamaJoukko.getChildren().get(1)).setFont(Font.font("Times New Roman", 16));
        asetaJoukonJäseneksi(vaihtama, vaihtamaJoukko, 0);
        lausekeLisäys.setOnMouseClicked(event -> {
            toimintaKohta.getItems().add("Lauseke " + Integer.toString(lausekeK[0]));
            if (lausekeK[0] >1) {
                viivat.add(new Separator());
                viivat.get(viivat.size()-1).setMinWidth(120);
                lausekeJoukko.getChildren().add(viivat.get(viivat.size()-1));
            }
            else {
                toimintaKohta.setDisable(false);
                toimintaKohta.setValue(toimintaKohta.getItems().get(0));
                sievennys.setDisable(false);
                laajennus.setDisable(false);
                vaihto.setDisable(false);
                modulaariLasku.setDisable(false);
            }
            nimikkeet.add(new Label("Lauseke " + Integer.toString(lausekeK[0])));
            lausekeJoukko.getChildren().add(nimikkeet.get(lausekeK[0] -1));
            lausekkeet.add(new ToggleText("tyhjä lauseke"));
            lausekkeet.get(lausekeK[0] -1).solid.setFont(Font.font("Times New Roman", FontPosture.ITALIC,22));
            asetaJoukonJäseneksi(lausekkeet, lausekeJoukko, lausekeK[0] -1, a -> 3*a+2);
            lausekeJoukko.getChildren().add(lausekkeet.get(lausekeK[0] -1).solid);
            lausekeK[0]++;
        });
        sievennys.setOnMouseClicked(event ->
                lausekkeet.get(Integer.parseInt(valittu.get(ruutuNumero)[0])-1).solid
                        .setText(lausekkeeksi(lausekkeet.get(Integer.parseInt(valittu.get(ruutuNumero)[0])-1).solid.getText()).sievetä().tekstiksi()));
        laajennus.setOnMouseClicked(event ->
                lausekkeet.get(Integer.parseInt(valittu.get(ruutuNumero)[0])-1).solid
                        .setText(lausekkeeksi(lausekkeet.get(Integer.parseInt(valittu.get(ruutuNumero)[0])-1).solid.getText()).laajentaaKerran().tekstiksi()));
        vaihto.setOnMouseClicked(event ->
                lausekkeet.get(Integer.parseInt(valittu.get(ruutuNumero)[0])-1).solid
                        .setText(lausekkeeksi(lausekkeet.get(Integer.parseInt(valittu.get(ruutuNumero)[0])-1).solid.getText()).vaihtaa(
                                lausekkeeksi(vaihdettava.solid.getText()), lausekkeeksi(vaihtama.solid.getText())
                        ).tekstiksi())
        );
        laskuModulus.setOnAction(event -> {
            String s = JOptionPane.showInputDialog("Syötä modulus");
            laskuModulus.setText(s);
        });
        modulaariLasku.setOnAction(event -> {
            Lauseke l = lausekkeeksi(lausekkeet.get(Integer.parseInt(valittu.get(ruutuNumero)[0])-1).solid.getText());
            int b = Integer.parseInt(laskuModulus.getText());
            modulusLasku(l, b);
            lausekkeet.get(Integer.parseInt(valittu.get(ruutuNumero)[0])-1).solid.setText(l.tekstiksi());
        });
        vaihtoJoukko.add(vaihto, 0, 0);
        vaihtoJoukko.add(erikoisLiitys, 0, 1);
        vaihtoJoukko.add(vaihdettava.edit, 1, 0); vaihtoJoukko.getChildren().remove(2);
        vaihtoJoukko.add(vaihdettava.solid, 1, 0);
        asetaJoukonJäseneksi(vaihdettava, vaihtoJoukko, 2);
        vaihtoJoukko.add(vaihtamaJoukko, 1, 1);
        näppäinJoukko1.getChildren().addAll(toimintaKohta, sievennys, laajennus, vaihtoJoukko);
        ListView parametriJoukko = new ListView<>(FXCollections.observableArrayList(""));
        parametriJoukko.setFixedCellSize(24); parametriJoukko.setPrefHeight(24);
        parametriJoukko.setCellFactory(l -> {
            TextFieldListCell<String> objekti = new TextFieldListCell<>();
            StringConverter<String> käännös = new StringConverter<String>() {
                @Override
                public String toString(String object) {
                    return object;
                }

                @Override
                public String fromString(String string) {
                    return string;
                }
            };
            objekti.setConverter(käännös);
            return objekti;
        });
        parametriJoukko.setEditable(true);
        Button asetus = new Button("Luo näillä");
        VBox ruutuObjekti1 = new VBox(parametriJoukko, asetus); ruutuObjekti1.setAlignment(Pos.CENTER);
        Scene ruutuNäkö1 = new Scene(ruutuObjekti1);
        Stage ruutu1 = new Stage(); ruutu1.setTitle("Liitä parametrit"); ruutu1.setScene(ruutuNäkö1);
        ruutu1.initOwner(primaryStage); ruutu1.initModality(Modality.APPLICATION_MODAL);
        EventHandler tapahtuma = parametriJoukko.getOnEditCommit();
        parametriJoukko.setOnEditCommit(new EventHandler<ListView.EditEvent>() {
            @Override
            public void handle(ListView.EditEvent event) {
                tapahtuma.handle(event);
                if (event.getIndex()==parametriJoukko.getItems().size()-1&&!event.getNewValue().equals("")) {
                    parametriJoukko.getItems().add("");
                    parametriJoukko.setPrefHeight(parametriJoukko.getFixedCellSize()*parametriJoukko.getItems().size()+2);
                    ruutu1.setHeight(ruutuObjekti1.getHeight()+asetus.getHeight()+36.67666666666666666666);
                }
            }
        });
        VBox näppäinJoukko2 = new VBox(5);
        vaihdettavat.add(new ArrayList<>());
        vaihtamat.add(new ArrayList<>());
        erikoisLiitys.setOnMouseClicked(event -> {
            parametriJoukko.getItems().clear();
            parametriJoukko.getItems().add("");
            asetus.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (erikoisK.get(ruutuNumero)<=1) näppäinJoukko2.getChildren().add(new Label("Erikoisvaihtofunktiot"));
                    int ordinaali = erikoisK.get(ruutuNumero)-1;
                    näppäinJoukko2.getChildren().add(new HBox(5));
                    Label yhtäMerkki = new Label("="); yhtäMerkki.setFont(Font.font("Times New Roman", 16));
                    vaihdettavat.get(ruutuNumero).add(new ToggleText(vaihdettava.solid.getText()));
                    vaihdettavat.get(ruutuNumero).get(ordinaali).solid.setFont(Font.font("Times New Roman", 16));
                    vaihtamat.get(ruutuNumero).add(new ToggleText(vaihtama.solid.getText()));
                    vaihtamat.get(ruutuNumero).get(ordinaali).solid.setFont(Font.font("Times New Roman", 16));
                    ((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)).getChildren().addAll(
                            new VBox(2.5, new Button("sovella"), new Button("poista")),
                            vaihdettavat.get(ruutuNumero).get(ordinaali).solid, yhtäMerkki, vaihtamat.get(ruutuNumero).get(ordinaali).solid
                    );
                    asetaErikoisPoistaminen(näppäinJoukko2, ordinaali, a -> a+1);
                    HBox parametrit = new HBox();
                    for (Object i : parametriJoukko.getItems().subList(0, parametriJoukko.getItems().size()-1)) {
                        Label l = new Label(" " + i + "=");
                        l.setFont(Font.font("Times New Roman", 16));
                        TextField t = new TextField();
                        t.setPrefWidth(25);
                        parametrit.getChildren().addAll(l, t);
                    }
                    ((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)).getChildren().add(parametrit);
                    asetaVaihdonSoveltaminen(valittu.get(ruutuNumero), näppäinJoukko2, lausekeJoukko, a -> 3*a+2, vaihdettavat.get(ruutuNumero),
                            vaihtamat.get(ruutuNumero), ordinaali, parametrit, a -> a+1);
                    asetaJoukonJäseneksi(vaihdettavat.get(ruutuNumero).get(ordinaali),
                            ((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)), 1);
                    asetaJoukonJäseneksi(vaihtamat.get(ruutuNumero).get(ordinaali),
                            ((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)), 3);
                    asetaYhtälönTäysKäännös(((HBox) näppäinJoukko2.getChildren().get(ordinaali+1)).getChildren().get(2),
                            vaihdettavat.get(ruutuNumero), vaihtamat.get(ruutuNumero), ordinaali);
                    erikoisK.set(ruutuNumero, erikoisK.get(ruutuNumero)+1);
                    ruutu1.close();
                }
            });
            ruutu1.showAndWait();
        });
        vaihtoFunktioidenTallennus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser tiedostoV = new FileChooser();
                tiedostoV.setTitle("Tallenna vaihtofunktiot");
                tiedostoV.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data Files", "*.dat"));
                tiedostoV.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
                File tiedosto = tiedostoV.showSaveDialog(primaryStage);
                if (tiedosto!=null) {
                    StringBuilder sisältö = new StringBuilder();
                    for (int i=0; i<näppäinJoukko2.getChildren().size()-1; i++) {
                        sisältö.append("§").append(vaihdettavat.get(ruutuNumero).get(i).solid.getText()).append(":").append(vaihtamat.get(ruutuNumero).get(i).solid.getText());
                        for (int j=0; j<((HBox) ((HBox) näppäinJoukko2.getChildren().get(i+1)).getChildren().get(4)).getChildren().size()-1; j+=2) {
                            String a = ((Label) ((HBox) ((HBox) näppäinJoukko2.getChildren().get(i+1)).getChildren().get(4)).getChildren().get(j)).getText();
                            sisältö.append(":").append(a, 1, a.length()-1);
                        }
                    }
                    byte data[] = sisältö.toString().getBytes();
                    try (OutputStream ulos = new BufferedOutputStream(Files.newOutputStream(tiedosto.toPath(), CREATE))) {
                        ulos.write(data, 0, data.length);
                    } catch (IOException x) {
                        System.err.print(x);
                    }
                }
            }
        });
        näkö.getChildren().addAll(lausekeJoukko, näppäinJoukko1, näppäinJoukko2);
        näkö1.getChildren().addAll(valikko, vieritys);
        VBox.setVgrow(vieritys, Priority.ALWAYS);
        Scene scene = new Scene(näkö1, 575, 275);
        primaryStage.setTitle("Laskin");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        luoRuutu(primaryStage);
    }

    interface LukuToiminta {
        int toiminta (int a);
    }

    public void asetaJoukonJäseneksi (ArrayList<ToggleText> lausekkeet, Pane joukko, int hakemisto) {
        asetaJoukonJäseneksi(lausekkeet, joukko, hakemisto, a -> a);
    }

    public void asetaJoukonJäseneksi (ArrayList<ToggleText> lausekkeet, Pane joukko, int hakemisto, LukuToiminta toiminta) {
        asetaJoukonJäseneksi(lausekkeet.get(hakemisto), joukko, toiminta.toiminta(hakemisto));
    }

    public void asetaJoukonJäseneksi (ToggleText lauseke, Pane joukko, int hakemisto) {
        lauseke.textboxshow = event1 -> {
            joukko.getChildren().remove(hakemisto);
            joukko.getChildren().add(hakemisto, lauseke.edit);
        };
        lauseke.textadapt = event1 -> {
            lauseke.edit.setText(lausekkeeksi(lauseke.edit.getText()).tekstiksi());
            joukko.getChildren().remove(hakemisto);
            joukko.getChildren().add(hakemisto, lauseke.solid);
        };
    }

    public void asetaYhtälönTäysKäännös (Node näpättävä, ArrayList<ToggleText> vaihdettavat, ArrayList<ToggleText> vaihtamat, int hakemisto) {
        näpättävä.setOnMouseClicked(event -> {
            String varaus = vaihdettavat.get(hakemisto).solid.getText();
            vaihdettavat.get(hakemisto).solid.setText(vaihtamat.get(hakemisto).solid.getText());
            vaihtamat.get(hakemisto).solid.setText(varaus);
        });
    }

    public void asetaVaihdonSoveltaminen (String[] valittu, Pane joukko, Pane lausekkeet, LukuToiminta toiminta1,
                                          ArrayList<ToggleText> vaihdettavat, ArrayList<ToggleText> vaihtamat,
                                          int hakemisto, Pane parametrit, LukuToiminta toiminta2) {
        ((VBox) ((HBox) joukko.getChildren().get(toiminta2.toiminta(hakemisto))).getChildren().get(0)).getChildren().get(0)
                .setOnMouseClicked(event ->
                        {
                            String x = vaihdettavat.get(hakemisto).solid.getText();
                            String y = vaihtamat.get(hakemisto).solid.getText();
                            for (int i=0; i<parametrit.getChildren().size()-1; i+=2) {
                                String a = sivuindeksiksi(((Label) parametrit.getChildren().get(i)).getText(), "ylä"); a = a.substring(1,a.length()-1);
                                String b = sivuindeksiksi(((TextField) parametrit.getChildren().get(i+1)).getText(), "ylä");
                                while (x.contains(a)) x = x.substring(0,x.indexOf(a)) + b + x.substring(x.indexOf(a)+a.length());
                                while (y.contains(a)) y = y.substring(0,y.indexOf(a)) + b + y.substring(y.indexOf(a)+a.length());
                            }
                            String finalX = x;
                            String finalY = y;
                            if (lausekkeet.getChildren().get(toiminta1.toiminta(Integer.parseInt(valittu[0])-1)).getClass().equals(Label.class)) {
                                Label lauseke = ((Label) lausekkeet.getChildren().get(toiminta1.toiminta(Integer.parseInt(valittu[0])-1)));
                                lauseke.setText(lausekkeeksi(lauseke.getText()).vaihtaa(lausekkeeksi(finalX), lausekkeeksi(finalY)).tekstiksi());
                            }
                            else if (lausekkeet.getChildren().get(toiminta1.toiminta(Integer.parseInt(valittu[0])-1)).getClass().equals(TextField.class)) {
                                TextField lauseke = ((TextField) lausekkeet.getChildren().get(toiminta1.toiminta(Integer.parseInt(valittu[0])-1)));
                                lauseke.setText(lausekkeeksi(lauseke.getText()).vaihtaa(lausekkeeksi(finalX), lausekkeeksi(finalY)).tekstiksi());
                            }
                        }
                );
    }

    public void asetaErikoisPoistaminen (Pane joukko, int hakemisto, LukuToiminta toiminta) {
        ((VBox) ((HBox) joukko.getChildren().get(toiminta.toiminta(hakemisto))).getChildren().get(0)).getChildren().get(1)
                .setOnMouseClicked(event -> ((HBox) joukko.getChildren().get(toiminta.toiminta(hakemisto))).getChildren().clear());
    }

    public void modulusLasku (Lauseke lauseke, int mod) {
        switch (lauseke.laji) {
            case "numero":
                while (lauseke.numero>mod/2) lauseke.numero-=mod;
                break;
            case "tekijät":
                for (int i=0; i<lauseke.komponentit.length; i++) modulusLasku(lauseke.komponentit[i], mod);
                break;
            case "termit":
                for (int i=0; i<lauseke.komponentit.length; i++) modulusLasku(lauseke.komponentit[i], mod);
                break;
            case "potenssi":
                if (lauseke.komponentit[1].laji.equals("numero")&&lauseke.komponentit[1].numero>0) modulusLasku(lauseke.komponentit[0], mod);
                break;
            default:
                break;
        }
    }

    class ToggleText extends Node {
        Label solid;
        TextField edit;
        EventHandler textadapt;
        EventHandler textboxshow;
        public ToggleText (String caption) {
            this.solid = new Label(caption);
            this.edit = new TextField();
            this.edit.setVisible(false);
            this.textadapt = event -> {};
            this.textboxshow = event -> {};
            this.solid.setOnMouseClicked(event -> {
                this.textboxshow.handle(event);
                this.edit.setText(this.solid.getText());
                this.solid.setVisible(false);
                this.edit.setVisible(true);
                this.edit.requestFocus();
            });
            this.edit.setOnMouseClicked(event -> {
                this.textadapt.handle(event);
                this.solid.setText(this.edit.getText());
                this.edit.setVisible(false);
                this.solid.setVisible(true);
            });
        }
    }

    class Lauseke {
        int numero;
        String nimike;
        Lauseke[] komponentit;
        String laji;
        public Lauseke (int numero) {
            this.numero = numero;
            this.laji = "numero";
        }
        public Lauseke (String nimike) {
            this.nimike = nimike;
            this.laji = "nimike";
        }
        public Lauseke (Lauseke[] komponentit, String merkki) {
            this.komponentit = komponentit;
            switch (merkki) {
                case "^":
                    this.laji = "potenssi";
                    break;
                case "*":
                    this.laji = "tekijät";
                    break;
                case "+":
                    this.laji = "termit";
                    break;
            }
        }

        public Lauseke saadaKantaluku () {return laji.equals("potenssi") ? komponentit[0] : this;}
        public Lauseke saadaEksponentti () {return laji.equals("potenssi") ? komponentit[1] : new Lauseke(1);}
        public Lauseke asettaaEksponentti (Lauseke eksponentti) {return new Lauseke(new Lauseke[]{saadaKantaluku(), eksponentti}, "^");}

        public void poistaaTerttua () {
            if ((laji.equals("potenssi")||laji.equals("tekijät")||laji.equals("termit"))&&komponentit.length==1) {
                laji = komponentit[0].laji;
                switch (laji) {
                    case "numero":
                        numero = komponentit[0].numero;
                        break;
                    case "nimike":
                        nimike = komponentit[0].nimike;
                        break;
                    default:
                        komponentit = komponentit[0].komponentit;
                        break;
                }
            }
        }

        public boolean yhtä (Lauseke verrattava) {
            if (!laji.equals(verrattava.laji)) return false;
            switch (laji) {
                case "numero":
                    return numero==verrattava.numero;
                case "nimike":
                    return nimike.equals(verrattava.nimike);
                case "potenssi":
                    return komponentit[0].yhtä(verrattava.komponentit[0])&&komponentit[1].yhtä(verrattava.komponentit[1]);
                default:
                    kierros:
                    for (Lauseke i : komponentit) {
                        for (Lauseke j : verrattava.komponentit)
                            if (i.yhtä(j)) continue kierros;
                        return false;
                    }
                    kierros:
                    for (Lauseke i : verrattava.komponentit) {
                        for (Lauseke j : komponentit)
                            if (i.yhtä(j)) continue kierros;
                        return false;
                    }
                    return true;
            }
        }

        public Lauseke sievetä () {
            poistaaTerttua();
            switch (laji) {
                case "potenssi":
                    Lauseke potenssi = new Lauseke(new Lauseke[]{komponentit[0].sievetä(), komponentit[1].sievetä()}, "^");
                    if (potenssi.komponentit[0].laji.equals("potenssi"))
                        potenssi = new Lauseke(new Lauseke[]{potenssi.komponentit[0].komponentit[0],
                                new Lauseke(new Lauseke[]{potenssi.komponentit[0].komponentit[1], potenssi.komponentit[1]}, "*").sievetä()}, "^");
                    if (potenssi.komponentit[0].laji.equals("numero")&&potenssi.komponentit[1].laji.equals("numero")&&potenssi.komponentit[1].numero>0) {
                        int x=1;
                        for (int i=0; i<potenssi.komponentit[1].numero; i++) x = x*potenssi.komponentit[0].numero;
                        return new Lauseke(x);
                    }
                    if (potenssi.komponentit[1].yhtä(new Lauseke(0))) return new Lauseke(1);
                    else if (potenssi.komponentit[1].yhtä(new Lauseke(1))) return potenssi.komponentit[0];
                    else return potenssi;
                case "tekijät":
                    ArrayList<Lauseke> tekijät = new ArrayList<>();
                    ArrayList<Lauseke> tekijät2 = new ArrayList<>();
                    ArrayList<Lauseke> tekijät3 = new ArrayList<>();
                    for (Lauseke i : komponentit) tekijät.add(i.sievetä());
                    for (Lauseke i : tekijät) {
                        if (i.laji.equals("tekijät")) tekijät2.addAll(Arrays.asList(i.komponentit));
                        else tekijät2.add(i);
                    }
                    kierros:
                    for (int i=0; i<tekijät2.size(); i++) {
                        for (int j=0; j<tekijät2.size(); j++) {
                            if (tekijät2.get(i).saadaKantaluku().yhtä(tekijät2.get(j).saadaKantaluku())) {
                                if (j<i) continue kierros;
                                else if (j==i) tekijät3.add(tekijät2.get(j));
                                else {
                                    tekijät3.set(tekijät3.size()-1,
                                    tekijät3.get(tekijät3.size()-1).asettaaEksponentti(
                                            new Lauseke(new Lauseke[]{tekijät3.get(tekijät3.size()-1).saadaEksponentti(), tekijät2.get(j).saadaEksponentti()}, "+")));
                                }
                            }
                        }
                    }
                    tekijät3.add(0, new Lauseke(1));
                    for (int i=1; i<tekijät3.size(); i++) {
                        if (tekijät3.get(i).sievetä().laji.equals("numero")) {
                            tekijät3.set(0, new Lauseke(tekijät3.get(0).numero*tekijät3.get(i).sievetä().numero));
                            tekijät3.remove(i--);
                        }
                    }
                    for (int i=0; i<tekijät3.size(); i++) {
                        tekijät3.set(i, tekijät3.get(i).sievetä());
                        if (tekijät3.get(i).yhtä(new Lauseke(0))) return new Lauseke(0);
                        else if (tekijät3.get(i).yhtä(new Lauseke(1))) if (tekijät3.size()>1) tekijät3.remove(i--);
                    }
                    return tekijät3.size()!=1 ? new Lauseke(tekijät3.toArray(new Lauseke[]{}), "*") : tekijät3.get(0);
                case "termit":
                    ArrayList<Lauseke> termit = new ArrayList<>();
                    ArrayList<Lauseke> termit2 = new ArrayList<>();
                    ArrayList<Lauseke> termit3 = new ArrayList<>();
                    for (Lauseke i : komponentit) termit.add(i.sievetä());
                    for (Lauseke i : termit) {
                        if (i.laji.equals("termit")) termit2.addAll(Arrays.asList(i.komponentit));
                        else termit2.add(i);
                    }
                    int laskettava;
                    ArrayList<Lauseke> vaihto;
                    kierros:
                    for (int i=0; i<termit2.size(); i++) {
                        for (int j=0; j<termit2.size(); j++) {
                            if (
                                    (termit2.get(i).laji.equals("tekijät")&&termit2.get(i).komponentit[0].laji.equals("numero") ?
                                            (termit2.get(i).komponentit.length>2 ?
                                                    new Lauseke(Arrays.copyOfRange(termit2.get(i).komponentit, 1, termit2.get(i).komponentit.length), "*")
                                                    : (termit2.get(i).komponentit.length==2 ? termit2.get(i).komponentit[1] : new Lauseke(0)))
                                            : termit2.get(i)).yhtä(termit2.get(j).laji.equals("tekijät")&&termit2.get(j).komponentit[0].laji.equals("numero") ?
                                            (termit2.get(j).komponentit.length>2 ?
                                                    new Lauseke(Arrays.copyOfRange(termit2.get(j).komponentit, 1, termit2.get(j).komponentit.length), "*")
                                                    : (termit2.get(j).komponentit.length==2 ? termit2.get(j).komponentit[1] : new Lauseke(0)))
                                            : termit2.get(j))
                                            ||(termit2.get(i).laji.equals("numero")&&termit2.get(j).laji.equals("numero"))
                                    ) {
                                if (j<i) continue kierros;
                                else if (j==i) termit3.add(termit2.get(i));
                                else {
                                    if (termit3.get(termit3.size()-1).laji.equals("numero")&&termit2.get(j).laji.equals("numero"))
                                        termit3.set(termit3.size()-1, new Lauseke(termit3.get(termit3.size()-1).numero+termit2.get(j).numero));
                                    else {
                                        laskettava = (termit2.get(j).laji.equals("tekijät")&&termit2.get(j).komponentit[0].laji.equals("numero") ?
                                                termit2.get(j).komponentit[0].numero : 1);
                                        if (termit3.get(termit3.size()-1).laji.equals("tekijät")&&termit3.get(termit3.size()-1).komponentit[0].laji.equals("numero"))
                                            termit3.get(termit3.size()-1).komponentit[0] = new Lauseke(termit3.get(termit3.size()-1).komponentit[0].numero+laskettava);
                                        else {
                                            vaihto = new ArrayList<>();
                                            vaihto.add(new Lauseke(laskettava+1));
                                            if (termit3.get(termit3.size()-1).laji.equals("tekijät")) Collections.addAll(vaihto, termit3.get(termit3.size()-1).komponentit);
                                            else vaihto.add(termit3.get(termit3.size()-1));
                                            termit3.set(termit3.size()-1, new Lauseke(vaihto.toArray(new Lauseke[]{}), "*"));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (int i=0; i<termit3.size(); i++) {
                        termit3.set(i, termit3.get(i).sievetä());
                        if (termit3.get(i).yhtä(new Lauseke(0))) if (termit3.size()>1) termit3.remove(i--);
                    }
                    return termit3.size()!=1 ? new Lauseke(termit3.toArray(new Lauseke[]{}), "+") : termit3.get(0);
                default:
                    return this;
            }
        }

        public void laajentaaPotenssi () {
            if (laji.equals("potenssi")&&komponentit[0].laji.equals("termit")&&komponentit[1].laji.equals("numero")&&komponentit[1].numero>0) {
                laji = "tekijät";
                Lauseke[] uudet = new Lauseke[komponentit[1].numero];
                Arrays.fill(uudet, komponentit[0]);
                komponentit = uudet;
            }
            if (laji.equals("potenssi")&&komponentit[0].laji.equals("tekijät")) {
                laji = "tekijät";
                Lauseke[] uudet = new Lauseke[komponentit[0].komponentit.length];
                for (int i=0; i<komponentit[0].komponentit.length; i++) uudet[i] = new Lauseke(new Lauseke[]{komponentit[0].komponentit[i], komponentit[1]}, "^");
                komponentit = uudet;
            }
        }

        public Lauseke laajentaaKerran () {
            laajentaaPotenssi();
            switch (laji) {
                case "tekijät":
                    for (Lauseke p : komponentit) p.laajentaaPotenssi();
                    ArrayList<ArrayList<Lauseke>> loppuvaihto = new ArrayList<>();
                    loppuvaihto.add(new ArrayList<>(Arrays.asList(komponentit)));
                    ArrayList<ArrayList<Lauseke>> vaihto = new ArrayList<>();
                    for (int i=0; i<komponentit.length; i++) {
                        if (!komponentit[i].laji.equals("termit")) continue;
                        for (int j=0; j<loppuvaihto.size(); j++) {
                            for (int k=0; k<komponentit[i].komponentit.length; k++) {
                                vaihto.add(new ArrayList<>());
                                for (int l=0; l<i; l++) vaihto.get(vaihto.size()-1).add(loppuvaihto.get(j).get(l));
                                vaihto.get(vaihto.size()-1).add(komponentit[i].komponentit[k]);
                                for (int l=i+1; l<komponentit.length; l++) vaihto.get(vaihto.size()-1).add(komponentit[l]);
                            }
                        }
                        loppuvaihto.clear();
                        loppuvaihto.addAll(vaihto);
                        vaihto.clear();
                    }
                    Lauseke[] tulos = new Lauseke[loppuvaihto.size()];
                    for (int i=0; i<tulos.length; i++) {
                        tulos[i] = new Lauseke(loppuvaihto.get(i).toArray(new Lauseke[]{}), "*");
                    }
                    Lauseke tulos2 = new Lauseke(tulos, "+");
                    tulos2.poistaaTerttua();
                    return tulos2;
                case "termit":
                    ArrayList<Lauseke> laajennus = new ArrayList<>();
                    for (Lauseke i : komponentit) {
                        if (i.laajentaaKerran().laji.equals("termit")) laajennus.addAll(Arrays.asList(i.laajentaaKerran().komponentit));
                        else laajennus.add(i);
                    }
                    return new Lauseke(laajennus.toArray(new Lauseke[]{}), "+");
                default:
                    return this;
            }
        }

        public Lauseke vaihtaa (Lauseke vaihdettava, Lauseke vaihtama) {
            if (sievetä().yhtä(vaihdettava.sievetä())) return vaihtama;
            ArrayList<Lauseke> uudet = new ArrayList<>();
            switch (laji) {
                case "potenssi":
                    return new Lauseke(new Lauseke[]{komponentit[0].vaihtaa(vaihdettava, vaihtama), komponentit[1].vaihtaa(vaihdettava, vaihtama)}, "^");
                case "tekijät":
                    for (Lauseke i : komponentit) uudet.add(i.vaihtaa(vaihdettava, vaihtama));
                    return new Lauseke(uudet.toArray(new Lauseke[]{}), "*");
                case "termit":
                    for (Lauseke i : komponentit) uudet.add(i.vaihtaa(vaihdettava, vaihtama));
                    return new Lauseke(uudet.toArray(new Lauseke[]{}), "+");
                default:
                    return this;
            }
        }

        public String tekstiksi () {
            String teksti;
            switch (laji) {
                case "numero":
                    if (numero==-1) return ".";
                    else if (numero<0) return "." + Integer.toString(-1*numero);
                    else return Integer.toString(numero);
                case "nimike":
                    return nimike;
                case "potenssi":
                    return "(" + komponentit[0].tekstiksi() + ")^(" + komponentit[1].tekstiksi() + ")";
                case "tekijät":
                    teksti = komponentit[0].tekstiksi();
                    for (int i=1; i<komponentit.length; i++) {
                        teksti = teksti;
                        if (!välikö(komponentit[i-1].tekstiksi().charAt(komponentit[i-1].tekstiksi().length()-1), komponentit[i].tekstiksi().charAt(0))) teksti = teksti + "*";
                        teksti = teksti + komponentit[i].tekstiksi();
                    }
                    return teksti;
                case "termit":
                    teksti = "(" + komponentit[0].tekstiksi();
                    for (int i=1; i<komponentit.length; i++) {
                        teksti = teksti + "+" + komponentit[i].tekstiksi();
                    }
                    return teksti + ")";
                default:
                    return "";
            }
        }
    }

    public boolean välikö (char t1, char t2) {
        String s1 = String.valueOf(t1);
        String s2 = String.valueOf(t2);
        String numero = "[0-9]";
        String symboli = "[a-zA-Z.]";
        String koriste = "[⁰¹²³⁴⁵⁶⁷⁸⁹₀₁₂₃₄₅₆₇₈₉ᵃᵇᶜᵈᵉᶠᵍʰⁱʲᵏˡᵐⁿᵒᵖʳˢᵗᵘᵛʷˣʸᶻᴬᴮᴰᴱᴳᴴᴵᴶᴷᴸᴹᴺᴼᴾᴿᵀᵁⱽᵂₐₑₕᵢⱼₖₗₘₙₒₚᵣₛₜᵤᵥₓ]";
        return !(s1.matches(numero)&&s2.matches(numero))&&!s2.matches(koriste)&&!(s1.matches("\\^")||s2.matches("\\^"));
    }

    public Lauseke lausekkeeksi (String teksti) {
        int hakemisto, toinenhakemisto;
        ArrayList<Lauseke> tulos = new ArrayList<>();
        if (teksti.equals("")) return new Lauseke(0);
        if (teksti.length()==1) return teksti.matches("[0-9]+") ? new Lauseke(Integer.parseInt(teksti)) :
                (teksti.equals(".") ? new Lauseke(-1) : new Lauseke(teksti));
        for (int s=0; s<teksti.length()-1; s++) {
            if (StringUtils.countMatches(teksti.substring(0,s+1), "(")==StringUtils.countMatches(teksti.substring(0,s+1), ")")) {
                if (teksti.contains("^")) {
                    kierros:
                    for (int n=1; StringUtils.ordinalIndexOf(teksti, "^", n)!=-1; n++) {
                        hakemisto = StringUtils.ordinalIndexOf(teksti, "^", n);
                        if (StringUtils.countMatches(teksti.substring(0, hakemisto), "(")==StringUtils.countMatches(teksti.substring(0, hakemisto), ")")) {
                            for (int t=0; t<hakemisto-1; t++) {
                                if (StringUtils.countMatches(teksti.substring(0,t+1), "(")==StringUtils.countMatches(teksti.substring(0,t+1), ")")) break kierros;
                            }
                            for (int t=hakemisto+1; t<teksti.length()-1; t++) {
                                if (StringUtils.countMatches(teksti.substring(hakemisto+1,t+1), "(")==StringUtils.countMatches(teksti.substring(hakemisto+1,t+1), ")")) break kierros;
                            }
                            return new Lauseke(new Lauseke[]{lausekkeeksi(teksti.substring(1,hakemisto-1)), lausekkeeksi(teksti.substring(hakemisto+2,teksti.length()-1))}, "^");
                        }
                    }
                }
                toinenhakemisto=0;
                for (int i=1; i<teksti.length(); i++) {
                    if (StringUtils.countMatches(teksti.substring(0, i), "(")==StringUtils.countMatches(teksti.substring(0, i), ")")) {
                        if (välikö(teksti.charAt(i-1), teksti.charAt(i))) {
                            tulos.add(lausekkeeksi(teksti.substring(toinenhakemisto, i)));
                            toinenhakemisto = i;
                        }
                        if (teksti.charAt(i)=='*') {
                            toinenhakemisto++;
                            i++;
                        }
                    }
                }
                if (tulos.size()==0) return teksti.matches("[0-9]+") ? new Lauseke(Integer.parseInt(teksti)) : new Lauseke(teksti);
                tulos.add(lausekkeeksi(teksti.substring(toinenhakemisto)));
                return new Lauseke(tulos.toArray(new Lauseke[]{}), "*");
            }
        }
        toinenhakemisto=1;
        for (int n=1; StringUtils.ordinalIndexOf(teksti, "+", n)!=-1; n++) {
            hakemisto = StringUtils.ordinalIndexOf(teksti, "+", n);
            if (StringUtils.countMatches(teksti.substring(1, hakemisto), "(")!=StringUtils.countMatches(teksti.substring(1, hakemisto), ")")) continue;
            tulos.add(lausekkeeksi(teksti.substring(toinenhakemisto, hakemisto)));
            toinenhakemisto = hakemisto+1;
        }
        tulos.add(lausekkeeksi(teksti.substring(toinenhakemisto, teksti.length()-1)));
        return new Lauseke(tulos.toArray(new Lauseke[]{}), "+");
    }

    public static void main(String[] args) {
        launch(args);
    }
}