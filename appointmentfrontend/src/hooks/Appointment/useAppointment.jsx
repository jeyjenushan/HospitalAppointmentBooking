import { useContext, useEffect, useState } from "react";
import { AppContext } from "../../context/AppContext";
import { useParams } from "react-router-dom";

export const useAppointment = () => {
  const { docId } = useParams();
  const {
    doctors,
    fetchDocInfo,
    docInfo,
    bookAppointment,
    patientId,
    getDoctosData,
  } = useContext(AppContext);

  const daysOfWeek = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"];
  const [docSlots, setDocSlots] = useState([]);
  const [slotIndex, setSlotIndex] = useState(0);
  const [slotTime, setSlotTime] = useState("");

  const getAvailableSlots = async () => {
    setDocSlots([]);

    // getting current date
    let today = new Date();

    for (let i = 0; i < 7; i++) {
      // getting date with index
      let currentDate = new Date(today);
      currentDate.setDate(today.getDate() + i);

      // setting end time of the date with index
      let endTime = new Date();
      endTime.setDate(today.getDate() + i);
      endTime.setHours(21, 0, 0, 0);

      // setting hours
      if (today.getDate() === currentDate.getDate()) {
        currentDate.setHours(
          currentDate.getHours() >= 10 ? currentDate.getHours() + 1 : 10
        );
        currentDate.setMinutes(currentDate.getMinutes() > 30 ? 30 : 0);
      } else {
        currentDate.setHours(10);
        currentDate.setMinutes(0);
      }

      let timeSlots = [];

      while (currentDate < endTime) {
        let formattedTime = currentDate.toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
          second: "2-digit",
        });

        let day = currentDate.getDate();
        let month = currentDate.getMonth() + 1;
        let year = currentDate.getFullYear();

        const slotDate = `${year}-${month < 10 ? "0" + month : month}-${
          day < 10 ? "0" + day : day
        }`; // Format to yyyy-MM-dd
        const slotTime = formattedTime;

        const isSlotAvailable = !(
          docInfo?.slots_booked?.[slotDate] || []
        ).includes(slotTime);

        // Add slot to array
        if (isSlotAvailable) {
          // Add slot to array
          timeSlots.push({
            datetime: new Date(currentDate),
            time: formattedTime,
          });
        }

        // Increment current time by 30 minutes
        currentDate.setMinutes(currentDate.getMinutes() + 30);
      }

      setDocSlots((prev) => [...prev, timeSlots]);
    }
  };

  const prepareBookingData = async () => {
    if (
      !docSlots ||
      docSlots.length <= slotIndex ||
      !docSlots[slotIndex].length
    ) {
      throw new Error("No available slots. Please refresh the page.");
    }

    const selectedSlot = docSlots[slotIndex][0];

    if (!selectedSlot || !selectedSlot.datetime || !selectedSlot.time) {
      throw new Error("Selected slot is invalid. Please select a valid slot.");
    }

    const dateObj = selectedSlot.datetime;
    let day = dateObj.getDate();
    let month = dateObj.getMonth() + 1;
    let year = dateObj.getFullYear();
    const slotDate = `${year}-${month < 10 ? "0" + month : month}-${
      day < 10 ? "0" + day : day
    }`;

    const bookingData = {
      doctorId: docId,
      patientId: patientId,
      date: slotDate,
      time: selectedSlot.time,
    };

    return bookingData;
  };

  useEffect(() => {
    if (doctors.length > 0) {
      fetchDocInfo(docId);
    }
  }, [doctors, docId]);

  useEffect(() => {
    if (docInfo) {
      getAvailableSlots();
    }
  }, [docInfo]);
  return {
    docInfo,
    docSlots,
    slotIndex,
    slotTime,
    daysOfWeek,
    setSlotIndex,
    setSlotTime,
    prepareBookingData,
    docId,
  };
};
