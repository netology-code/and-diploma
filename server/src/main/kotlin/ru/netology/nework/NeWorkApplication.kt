package ru.netology.nework

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.util.ResourceUtils
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Post
import ru.netology.nework.enumeration.AttachmentType
import ru.netology.nework.enumeration.EventType
import ru.netology.nework.service.EventService
import ru.netology.nework.service.PostService
import ru.netology.nework.service.UserService
import java.time.OffsetDateTime

@SpringBootApplication
class NeWorkApplication {
    @Bean
    fun runner(
        userService: UserService,
        postService: PostService,
        eventService: EventService,
        @Value("\${app.media-location}") mediaLocation: String,
    ) = CommandLineRunner {
        ResourceUtils.getFile("classpath:static").copyRecursively(
            ResourceUtils.getFile(mediaLocation),
            true,
        )

        val netology = userService.create(login = "netology", pass = "secret", name = "Netology", avatar = "netology.jpg")
        val sber = userService.create(login = "sber", pass = "secret", name = "Сбер", avatar = "sber.jpg")
        val tcs = userService.create(login = "tcs", pass = "secret", name = "Тинькофф", avatar = "tcs.jpg")
        val got = userService.create(login = "got", pass = "secret", name = "Game of Thrones", avatar = "got.jpg")
        val soundhelix = userService.create(login = "soundhelix", pass = "secret", name = "Sound Helix", avatar = "soundhelix.png")
        val student = userService.create(login = "student", pass = "secret", name = "Студент", avatar = "netology.jpg")

        userService.saveInitialToken(student.id, "x-token")

        val firstEvent = eventService.saveInitial(
            Event(
                id = 0,
                authorId = netology.id,
                author = netology.name,
                authorAvatar = netology.avatar,
                content = "Конференция Netology.ONLINE",
                datetime = OffsetDateTime.now().plusMonths(2).toEpochSecond(),
                published = 0,
                type = EventType.ONLINE,
                speakerIds = setOf(netology.id, student.id),
                attachment = Attachment(
                    type = AttachmentType.IMAGE,
                    url = "netology.jpg",
                )
            )
        )
        val secondEvent = eventService.saveInitial(
            Event(
                id = 0,
                authorId = sber.id,
                author = sber.name,
                authorAvatar = sber.avatar,
                content = "Встреча в главном офисе СберБанка",
                datetime = OffsetDateTime.now().plusMonths(1).toEpochSecond(),
                published = 0,
                coords = 55.77391788231168 to 37.60555493068535 ,
                type = EventType.OFFLINE,
                speakerIds = setOf(netology.id, student.id),
                attachment = Attachment(
                    type = AttachmentType.IMAGE,
                    url = "netology.jpg",
                )
            )
        )

        val firstPost = postService.saveInitial(
            Post(
                id = 0,
                authorId = netology.id,
                author = netology.name,
                authorAvatar = netology.avatar,
                content = "Привет, это новая Нетология!",
                published = 0,
                link = "https://l.netology.ru/new-netology",
                attachment = Attachment(
                    type = AttachmentType.IMAGE,
                    url = "update.png",
                )
            )
        )
        val secondPost = postService.saveInitial(
            Post(
                id = 0,
                authorId = sber.id,
                author = sber.name,
                authorAvatar = sber.avatar,
                content = "Привет, это новый Сбер!",
                published = 0,
            )
        )
        val thirdPost = postService.saveInitial(
            Post(
                id = 0,
                authorId = tcs.id,
                author = tcs.name,
                authorAvatar = tcs.avatar,
                content = "Нам и так норм!",
                published = 0,
            )
        )
        val fourthPost = postService.saveInitial(
            Post(
                id = 0,
                authorId = netology.id,
                author = netology.name,
                authorAvatar = netology.avatar,
                content = "Подкасты любят за возможность проводить время с пользой и слушать познавательные лекции или беседы во время прогулок или домашних дел. Интересно, что запустить свой подкаст и обсуждать интересные темы может любой.",
                published = 0,
                attachment = Attachment(
                    url = "podcast.jpg",
                    type = AttachmentType.IMAGE,
                ),
            )
        )
        val fifthPost = postService.saveInitial(
            Post(
                id = 0,
                authorId = sber.id,
                author = sber.name,
                authorAvatar = sber.avatar,
                content = "Появился новый способ мошенничества \uD83D\uDE21 Злоумышленники звонят от имени банка и говорят, что для клиента выпущена новая, особо защищённая карта, которую можно добавить в приложение Кошелёк на смартфоне. Под диктовку мошенника человек привязывает к Кошельку его карту, причём указывает своё имя. Если карту пополнить, деньги уйдут мошеннику.\n\nДело в том, что в Кошелёк можно добавить любую, даже чужую карту, а имя поставить какое угодно. Но чужая банковская карта не будет отображаться, например, в СберБанк Онлайн.",
                published = 0,
                attachment = Attachment(
                    url = "sbercard.jpg",
                    type = AttachmentType.IMAGE,
                ),
            )
        )
        val sixthPost = postService.saveInitial(
            Post(
                id = 0,
                authorId = soundhelix.id,
                author = soundhelix.name,
                authorAvatar = soundhelix.avatar,
                content = "New Music!",
                published = 0,
                attachment = Attachment(
                    url = "soundhelix.mp3",
                    type = AttachmentType.AUDIO,
                ),
            )
        )
        val seventhPost = postService.saveInitial(
            Post(
                id = 0,
                authorId = netology.id,
                author = netology.name,
                authorAvatar = netology.avatar,
                content = "Digital Start",
                published = 0,
                link = "https://netology.ru/programs/digital-padavan",
                attachment = Attachment(
                    url = "max.mp3",
                    type = AttachmentType.VIDEO,
                ),
            )
        )
        val eightsPost = postService.saveInitial(
            Post(
                id = 0,
                authorId = netology.id,
                author = netology.name,
                authorAvatar = netology.avatar,
                content = "Конференция Netology.ONLINE",
                published = 0,
                link = "/events/${firstEvent.id}",
            )
        )
        val ninth= postService.saveInitial(
            Post(
                id = 0,
                authorId = netology.id,
                author = netology.name,
                authorAvatar = netology.avatar,
                content = "Конференция Netology.ONLINE",
                published = 0,
                link = "nework://events/${firstEvent.id}",
            )
        )
    }
}

fun main(args: Array<String>) {
    runApplication<NeWorkApplication>(*args)
}
