package uk.gov.dwp.uc.pairtest;

import lombok.RequiredArgsConstructor;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;


@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public static int amount;
    public static int noOfSeats;
    private static int totalNumberOfTickets;

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validate(accountId <= 0);

        boolean isAdultAccompanied = false;

        for (TicketTypeRequest request : ticketTypeRequests) {
            switch (request.getTicketType()) {
                case ADULT:
                    isAdultAccompanied = true;
                    calculateAmountAndSeats(request, 20);
                    break;
                case CHILD:
                    calculateAmountAndSeats(request, 10);
                    break;
                case INFANT:
                    totalNumberOfTickets = totalNumberOfTickets + request.getNoOfTickets();
                    break;
                default:
            }
        }

        validate(!isAdultAccompanied);
        validate(totalNumberOfTickets > 20);

        ticketPaymentService.makePayment(accountId, amount);
        seatReservationService.reserveSeat(accountId, noOfSeats);

    }

    private void validate(boolean value) {
        if (value) {
            throw new InvalidPurchaseException();
        }
    }

    private void calculateAmountAndSeats(TicketTypeRequest request, int price) {
        amount += request.getNoOfTickets() * price;
        noOfSeats += request.getNoOfTickets();
        totalNumberOfTickets += request.getNoOfTickets();
    }

}
