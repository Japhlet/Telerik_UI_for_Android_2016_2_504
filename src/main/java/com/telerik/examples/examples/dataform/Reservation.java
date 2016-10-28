package com.telerik.examples.examples.dataform;

import com.telerik.examples.R;
import com.telerik.widget.dataform.engine.NonEmptyValidator;
import com.telerik.widget.dataform.engine.NotifyPropertyChangedBase;
import com.telerik.widget.dataform.engine.PhoneValidator;
import com.telerik.widget.dataform.visualization.annotations.DataFormProperty;
import com.telerik.widget.dataform.visualization.annotations.DataFormValidatorParams;
import com.telerik.widget.dataform.visualization.editors.DataFormDateEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormNumberPickerEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormSpinnerEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormTimeEditor;

import java.util.Calendar;

public class Reservation extends NotifyPropertyChangedBase {
    private String creatorName = "";
    private String creatorPhone = "";
    private long reservationDate = Calendar.getInstance().getTimeInMillis();
    private long reservationTime = DataFormFragment.getCalendarTime(20, 0);
    private int numberOfGuests = 1;
    private String tableSection = "patio";
    private int tableNumber = 1;
    private String origin = "in-person";
    private boolean cancelled;

    @DataFormProperty(label = "",
            index = 0,
            hint = "Name",
            editorLayout = R.layout.reservation_editor_layout_name,
            validator = NonEmptyValidator.class,
            validatorParams = @DataFormValidatorParams(negativeMessage = "Please fill in the guest name"),
            group = "RESERVATION DETAILS")
    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
        notifyListeners("CreatorName", creatorName);
    }

    @DataFormProperty(label = "",
            index = 1,
            hint = "Phone",
            validator = PhoneValidator.class,
            validatorParams = @DataFormValidatorParams(negativeMessage = "Please fill in a valid number"),
            editorLayout = R.layout.reservation_editor_layout_phone,
            coreEditorLayout = R.layout.reservation_phone_editor,
            group = "RESERVATION DETAILS")
    public String getCreatorPhone() {
        return creatorPhone;
    }

    public void setCreatorPhone(String creatorPhone) {
        this.creatorPhone = creatorPhone;
        notifyListeners("CreatorPhone", creatorPhone);
    }

    @DataFormProperty(label = "Date",
            index = 2,
            editor = DataFormDateEditor.class,
            validator = FutureDateValidator.class,
            group = "Reservation Date")
    public long getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(long reservationDate) {
        this.reservationDate = reservationDate;
        notifyListeners("ReservationDate", reservationDate);
    }

    @DataFormProperty(label = "Time",
            index = 2,
            editor = DataFormTimeEditor.class,
            group = "Reservation Date")
    public long getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(long reservationTime) {
        this.reservationTime = reservationTime;
        notifyListeners("ReservationTime", reservationTime);
    }

    @DataFormProperty(label = "",
            index = 3,
            editor = DataFormNumberPickerEditor.class,
            editorLayout = R.layout.reservation_editor_layout_guests,
            group = "group3")
    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        notifyListeners("NumberOfGuests", numberOfGuests);
    }

    @DataFormProperty(label = "Section",
            index = 4,
            editor = DataFormSpinnerEditor.class,
            coreEditorLayout = R.layout.reservation_spinner_editor,
            headerLayout = R.layout.reservation_table_editor_header,
            group = "TABLE DETAILS")
    public String getTableSection() {
        return tableSection;
    }

    public void setTableSection(String tableSection) {
        this.tableSection = tableSection;
        notifyListeners("TableSection", tableSection);
    }

    @DataFormProperty(label = "Table",
            index = 5,
            editor = DataFormSpinnerEditor.class,
            columnIndex = 1,
            coreEditorLayout = R.layout.reservation_spinner_editor,
            headerLayout = R.layout.reservation_table_editor_header,
            group = "TABLE DETAILS")
    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
        notifyListeners("TableNumber", tableNumber);
    }

    @DataFormProperty(index = 6,
            editorLayout = R.layout.reservation_editor_layout_no_image,
            editor = DataFormSpinnerEditor.class,
            group = "group5")
    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
        notifyListeners("Origin", origin);
    }

    @DataFormProperty(editor = CancelButtonEditor.class,
            editorLayout = R.layout.reservation_editor_layout_no_image,
            index = 7,
            group = "group5")
    public boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        notifyListeners("Cancelled", cancelled);
    }
}
