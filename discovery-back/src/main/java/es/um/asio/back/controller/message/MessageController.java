package es.um.asio.back.controller.message;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Message controller.
 */
@RestController
@RequestMapping(MessageController.Mappings.BASE)
public class MessageController {

    private MessageController() {}


    /**
     * Save.
     *
     * @param userDto
     *            the user dto
     * @return the application user dto
     * @throws TripleStoreException
     *             in case of error
     */

    /**
     * Mappgins.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Mappings {
        /**
         * Controller request mapping.
         */
        protected static final String BASE = "/message";

    }
}
