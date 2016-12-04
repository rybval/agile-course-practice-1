package ru.unn.agile.personalfinance.view.controllers;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.converter.CurrencyStringConverter;
import ru.unn.agile.PersonalFinance.Model.Transfer;
import ru.unn.agile.PersonalFinance.ViewModel.AccountViewModel;
import ru.unn.agile.PersonalFinance.ViewModel.TransferViewModel;
import ru.unn.agile.personalfinance.view.WindowsManager;
import ru.unn.agile.personalfinance.view.controls.StringListCellFactory;
import ru.unn.agile.personalfinance.view.utils.Converters;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTransferController extends DataContextController {
    private final static StringListCellFactory<AccountViewModel> accountListCellFactory =
            new StringListCellFactory<>(account -> account.getName());

    @FXML
    private JFXComboBox<AccountViewModel> accountFromComboBox;

    @FXML
    private JFXComboBox<AccountViewModel> accountToComboBox;

    @FXML
    private TextField amountField;

    @FXML
    public Button addButton;

    @FXML
    protected void handleAddButtonAction(final ActionEvent actionEvent) {
        ((TransferViewModel) getDataContext()).save();
        WindowsManager.getInstance().goBack();
    }

    @FXML
    protected void handleCancelButtonAction(final ActionEvent actionEvent) {
        WindowsManager.getInstance().goBack();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setUpAccountComboBox(accountFromComboBox, 0);
        setUpAccountComboBox(accountToComboBox, 1);
    }

    @Override
    protected void removeBindings(final Object oldDataContext) {
        final TransferViewModel oldTransfer = (TransferViewModel) oldDataContext;
        Bindings.unbindBidirectional(amountField.textProperty(), oldTransfer.amountProperty());
        oldTransfer.accountFromProperty().unbind();
        oldTransfer.accountToProperty().unbind();
        addButton.disableProperty().unbind();
    }

    @Override
    protected void addBindings(final Object newDataContext) {
        final TransferViewModel newTransfer = (TransferViewModel) newDataContext;

        /* amountField.text <-> transfer.amount */
        Bindings.bindBidirectional(
                amountField.textProperty(),
                newTransfer.amountProperty(),
                new CurrencyStringConverter());

        /* accountFromComboBox.selected -> transfer.accountFrom */
        ReadOnlyObjectProperty<AccountViewModel> selectedAccountFromProperty =
                accountFromComboBox.getSelectionModel().selectedItemProperty();
        newTransfer.accountFromProperty().bind(selectedAccountFromProperty);

        /* accountFromComboBox.selected -> transfer.accountTo */
        ReadOnlyObjectProperty<AccountViewModel> selectedAccountToProperty =
                accountToComboBox.getSelectionModel().selectedItemProperty();
        newTransfer.accountToProperty().bind(selectedAccountToProperty);

        /* transfer.isAbleToSave -> addButton.disabled */
        addButton.disableProperty().bind(newTransfer.isAbleToSaveProperty().not());
    }

    private void setUpAccountComboBox(final ComboBox<AccountViewModel> comboBox,
                                      final int selectedIndex) {
        comboBox.setCellFactory(accountListCellFactory);
        comboBox.setConverter(Converters.getAccountToStringConverter());
        comboBox.getSelectionModel().select(selectedIndex);
    }
}
