import React from "react";

const BookingSlots = ({
  docSlots,
  slotIndex,
  slotTime,
  daysOfWeek,
  setSlotIndex,
  setSlotTime,
  handleBookAppointment,
}) => {
  return (
    <div className="sm:ml-72 sm:pl-4 mt-4 font-medium text-gray-700">
      <p>Booking slots</p>
      <div className="flex gap-3 items-center w-full overflow-x-scroll mt-4">
        {docSlots.length &&
          docSlots.map((item, index) => (
            <div
              onClick={() => setSlotIndex(index)}
              key={index}
              className={`text-center py-6 min-w-16 rounded-full cursor-pointer ${
                slotIndex === index
                  ? "bg-primary text-white"
                  : "border border-gray-200"
              }`}
            >
              <p>{item[0] && daysOfWeek[item[0].datetime.getDay()]}</p>
              <p>{item[0] && item[0].datetime.getDate()}</p>
            </div>
          ))}
      </div>

      <div className="flex items-center gap-3 w-full overflow-x-scroll mt-4">
        {docSlots.length &&
          docSlots[slotIndex].map((item, index) => (
            <p
              onClick={() => setSlotTime(item.time)}
              key={index}
              className={`text-sm font-light flex-shrink-0 px-5 py-2 rounded-full cursor-pointer ${
                item.time === slotTime
                  ? "bg-primary text-white"
                  : "text-gray-400 border border-gray-300"
              }`}
            >
              {item.time.toLowerCase()}
            </p>
          ))}
      </div>

      <button
        onClick={handleBookAppointment}
        className="bg-primary text-white text-sm font-light px-14 py-3 rounded-full my-6"
      >
        Book an appointment
      </button>
    </div>
  );
};

export default BookingSlots;
