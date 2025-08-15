package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.*;
import com.example.travelbuddybackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Specification Pattern for Search and Filter Operations
 *
 * This approach uses the Specification pattern to create reusable, composable
 * search criteria. Think of specifications as building blocks that you can
 * combine in different ways to create complex searches.
 *
 * Benefits:
 * - Highly reusable search logic
 * - Easy to combine criteria (AND, OR, NOT operations)
 * - Type-safe and compile-time checked
 * - No code duplication
 * - Very readable and maintainable
 *
 * This is like having a set of Lego blocks that you can combine to build
 * any kind of search you need.
 */
@Service
public class SearchAndFilterService {

    private final ClientRepository clientRepository;
    private final FlightDetailsRepository flightDetailsRepository;
    private final TrainDetailsRepository trainDetailsRepository;
    private final BusDetailsRepository busDetailsRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public SearchAndFilterService(ClientRepository clientRepository,
                                  FlightDetailsRepository flightDetailsRepository,
                                  TrainDetailsRepository trainDetailsRepository,
                                  BusDetailsRepository busDetailsRepository,
                                  BookingRepository bookingRepository) {
        this.clientRepository = clientRepository;
        this.flightDetailsRepository = flightDetailsRepository;
        this.trainDetailsRepository = trainDetailsRepository;
        this.busDetailsRepository = busDetailsRepository;
        this.bookingRepository = bookingRepository;
    }

    // ============================================================================
    // CLIENT SPECIFICATIONS - Reusable search building blocks
    // ============================================================================

    /**
     * Client specification builder class
     * This creates reusable search criteria that can be combined
     */
    public static class ClientSpecs {

        public static Specification<Client> usernameContains(String username) {
            return client -> username == null ||
                    client.getUsername().toLowerCase().contains(username.toLowerCase());
        }

        public static Specification<Client> emailContains(String email) {
            return client -> email == null ||
                    client.getEmail().toLowerCase().contains(email.toLowerCase());
        }

        public static Specification<Client> nameContains(String name) {
            return client -> name == null ||
                    client.getFullName().toLowerCase().contains(name.toLowerCase());
        }

        public static Specification<Client> phoneContains(String phone) {
            return client -> phone == null ||
                    client.getPhone() != null && client.getPhone().contains(phone);
        }

        public static Specification<Client> addressContains(String address) {
            return client -> address == null ||
                    client.getAddress().toLowerCase().contains(address.toLowerCase());
        }

        public static Specification<Client> creditCardContains(String credit_card) {
            return client -> credit_card == null ||
                    client.getCredit_card() != null && client.getCredit_card().contains(credit_card);
        }

        public static Specification<Client> isEnabled(Boolean enabled) {
            return client -> enabled == null || client.isEnabled() == enabled;
        }

        public static Specification<Client> isNotLocked() {
            return client -> !client.isAccountLocked();
        }

        public static Specification<Client> emailDomainIs(String domain) {
            return client -> domain == null ||
                    (client.getEmail() != null && client.getEmail().toLowerCase().endsWith(domain.toLowerCase()));
        }
    }

    /**
     * Search clients using composable specifications
     *
     * Example usage:
     * - Find active clients: searchClients(ClientSpecs.isEnabled(true))
     * - Find users with "john" in name: searchClients(ClientSpecs.nameContains("john"))
     * - Complex search: searchClients(ClientSpecs.isEnabled(true).and(ClientSpecs.emailDomainIs("company.com")))
     */
    public List<Client> searchClients(Specification<Client> spec) {
        return clientRepository.findAll().stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }

    /**
     * Convenient method for searching clients by multiple criteria
     */
    public List<Client> searchClients(String username, String email, String name, Boolean enabled) {
        Specification<Client> spec = ClientSpecs.usernameContains(username)
                .and(ClientSpecs.emailContains(email))
                .and(ClientSpecs.nameContains(name))
                .and(ClientSpecs.isEnabled(enabled));

        return searchClients(spec);
    }

    // ============================================================================
    // FLIGHT SPECIFICATIONS - Reusable flight search building blocks
    // ============================================================================

    public static class FlightSpecs {

        public static Specification<FlightDetails> flightNumberContains(String flightNumber) {
            return flight -> flightNumber == null ||
                    flight.getFlightNumber().toLowerCase().contains(flightNumber.toLowerCase());
        }

        public static Specification<FlightDetails> originContains(String origin) {
            return flight -> origin == null ||
                    flight.getFlightOrigin().toLowerCase().contains(origin.toLowerCase());
        }

        public static Specification<FlightDetails> destinationContains(String destination) {
            return flight -> destination == null ||
                    flight.getFlightDestination().toLowerCase().contains(destination.toLowerCase());
        }

        public static Specification<FlightDetails> airlineContains(String airline) {
            return flight -> airline == null ||
                    (flight.getFlightAirline() != null && flight.getFlightAirline().toLowerCase().contains(airline.toLowerCase()));
        }

        public static Specification<FlightDetails> routeIs(String origin, String destination) {
            return originContains(origin).and(destinationContains(destination));
        }

        public static Specification<FlightDetails> priceInRange(Double minPrice, Double maxPrice) {
            return flight -> {
                if (flight.getFlightPrice() == null) return false;
                boolean aboveMin = minPrice == null || flight.getFlightPrice() >= minPrice;
                boolean belowMax = maxPrice == null || flight.getFlightPrice() <= maxPrice;
                return aboveMin && belowMax;
            };
        }
    }

    /**
     * Search flights using specifications
     */
    public List<FlightDetails> searchFlights(Specification<FlightDetails> spec) {
        return flightDetailsRepository.findAll().stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }

    /**
     * Find flights by route using specifications
     */
    public List<FlightDetails> findFlightsByRoute(String origin, String destination) {
        return searchFlights(FlightSpecs.routeIs(origin, destination));
    }

    /**
     * Find budget flights under a certain price
     */
    public List<FlightDetails> findBudgetFlights(Double maxPrice) {
        return searchFlights(FlightSpecs.priceInRange(null, maxPrice));
    }

    // ============================================================================
    // UNIVERSAL SEARCH using specifications
    // ============================================================================

    /**
     * Universal search using specifications
     * This demonstrates how to search across all entity types with one method
     */
    public UniversalSearchResults universalSearch(String searchTerm) {
        UniversalSearchResults results = new UniversalSearchResults();

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return results;
        }

        // Search clients using OR combination of specifications
        Specification<Client> clientSpec = ClientSpecs.usernameContains(searchTerm)
                .or(ClientSpecs.emailContains(searchTerm))
                .or(ClientSpecs.nameContains(searchTerm))
                .or(ClientSpecs.phoneContains(searchTerm));
        results.setClients(searchClients(clientSpec));

        // Search flights using OR combination of specifications
        Specification<FlightDetails> flightSpec = FlightSpecs.flightNumberContains(searchTerm)
                .or(FlightSpecs.originContains(searchTerm))
                .or(FlightSpecs.destinationContains(searchTerm))
                .or(FlightSpecs.airlineContains(searchTerm));
        results.setFlights(searchFlights(flightSpec));

        // Search bookings (you can create BookingSpecs similarly)
        results.setBookings(searchBookingsByTerm(searchTerm));

        return results;
    }

    /**
     * Example of a complex search using specification combinations
     * Find premium flights from major airports
     */
    public List<FlightDetails> findPremiumFlightsFromMajorAirports() {
        // This demonstrates the power of combining specifications
        Specification<FlightDetails> spec = FlightSpecs.priceInRange(500.0, null)  // Expensive flights
                .and(FlightSpecs.originContains("International"))                    // From international airports
                .and(FlightSpecs.airlineContains("Emirates")                         // Premium airlines
                        .or(FlightSpecs.airlineContains("Singapore"))
                        .or(FlightSpecs.airlineContains("Lufthansa")));

        return searchFlights(spec);
    }

    // ============================================================================
    // SPECIFICATION INTERFACE - The core abstraction
    // ============================================================================

    /**
     * The Specification interface
     * This is the core building block that makes everything work
     */
    @FunctionalInterface
    public interface Specification<T> extends Predicate<T> {

        /**
         * Check if an entity satisfies this specification
         */
        boolean isSatisfiedBy(T entity);

        /**
         * Default implementation to bridge with Predicate interface
         */
        @Override
        default boolean test(T entity) {
            return isSatisfiedBy(entity);
        }

        /**
         * Combine this specification with another using AND logic
         */
        default Specification<T> and(Specification<T> other) {
            return entity -> this.isSatisfiedBy(entity) && other.isSatisfiedBy(entity);
        }

        /**
         * Combine this specification with another using OR logic
         */
        default Specification<T> or(Specification<T> other) {
            return entity -> this.isSatisfiedBy(entity) || other.isSatisfiedBy(entity);
        }

        /**
         * Negate this specification (NOT logic)
         */
        default Specification<T> not() {
            return entity -> !this.isSatisfiedBy(entity);
        }
    }

    // ============================================================================
    // HELPER METHODS AND FALLBACKS
    // ============================================================================

    /**
     * Fallback method for booking search (until you create BookingSpecs)
     */
    private List<Booking> searchBookingsByTerm(String searchTerm) {
        return bookingRepository.findAll().stream()
                .filter(booking ->
                        booking.getBookingId().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                booking.getClientName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                booking.getClientEmail().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    // ============================================================================
    // RESULT CLASSES - Same as before
    // ============================================================================

    public static class UniversalSearchResults {
        private List<Client> clients;
        private List<FlightDetails> flights;
        private List<Booking> bookings;

        // Constructor
        public UniversalSearchResults() {
            this.clients = new java.util.ArrayList<>();
            this.flights = new java.util.ArrayList<>();
            this.bookings = new java.util.ArrayList<>();
        }

        // Getters and setters
        public List<Client> getClients() { return clients; }
        public void setClients(List<Client> clients) { this.clients = clients; }

        public List<FlightDetails> getFlights() { return flights; }
        public void setFlights(List<FlightDetails> flights) { this.flights = flights; }

        public List<Booking> getBookings() { return bookings; }
        public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

        public int getTotalResults() {
            return clients.size() + flights.size() + bookings.size();
        }
    }
}

/**
 * Example Usage in a Controller or Test:
 *
 * // Simple searches
 * List<Client> activeClients = searchService.searchClients(ClientSpecs.isEnabled(true));
 * List<Client> johnUsers = searchService.searchClients(ClientSpecs.nameContains("john"));
 *
 * // Complex searches using AND/OR combinations
 * List<Client> corporateUsers = searchService.searchClients(
 *     ClientSpecs.isEnabled(true)
 *         .and(ClientSpecs.emailDomainIs("company.com"))
 *         .and(ClientSpecs.isNotLocked())
 * );
 *
 * // Flight searches
 * List<FlightDetails> nyToLaFlights = searchService.searchFlights(
 *     FlightSpecs.originContains("New York")
 *         .and(FlightSpecs.destinationContains("Los Angeles"))
 * );
 *
 * // Budget flights under $300
 * List<FlightDetails> budgetFlights = searchService.findBudgetFlights(300.0);
 *
 * // Universal search
 * UniversalSearchResults results = searchService.universalSearch("john");
 */