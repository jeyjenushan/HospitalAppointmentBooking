import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { useAppointment } from "../../hooks/Appointment/useAppointment";
import { AppContext } from "../../context/AppContext";
import RelatedDoctors from "../../components/Appointments/RelatedDoctors";
import DoctorDetails from "../../components/Appointments/DoctorDetails";
import BookingSlots from "../../components/Appointments/BookingSlots";
const Appointment = () => {
  const navigate = useNavigate();
  const { token, bookAppointment, currencySymbol } = useContext(AppContext);
  const {
    docInfo,
    docSlots,
    slotIndex,
    slotTime,
    daysOfWeek,
    setSlotIndex,
    setSlotTime,
    prepareBookingData,
    docId,
  } = useAppointment();

  const handleBookAppointment = async () => {
    if (!token) {
      toast.warning("Login to book appointment");
      return navigate("/login");
    }

    const bookingData = await prepareBookingData();

    await bookAppointment(bookingData);
  };

  return docInfo ? (
    <div>
      {/* ---------- Doctor Details ----------- */}
      <DoctorDetails docInfo={docInfo} currencySymbol={currencySymbol} />

      {/* Booking slots */}
      <BookingSlots
        docSlots={docSlots}
        slotIndex={slotIndex}
        slotTime={slotTime}
        daysOfWeek={daysOfWeek}
        setSlotIndex={setSlotIndex}
        setSlotTime={setSlotTime}
        handleBookAppointment={handleBookAppointment}
      />

      {/* Listing Related Doctors */}
      <RelatedDoctors specialization={docInfo.specialization} docId={docId} />
    </div>
  ) : null;
};

export default Appointment;
