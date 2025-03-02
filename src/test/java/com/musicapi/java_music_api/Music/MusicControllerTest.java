package com.musicapi.java_music_api.Music;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Description;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.musicapi.java_music_api.JavaMusicApiApplication;
import com.musicapi.java_music_api.music.Music;
import com.musicapi.java_music_api.music.MusicService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = JavaMusicApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MusicControllerTest {

    @LocalServerPort
    private int port;

    private URI baseURI;

    @Autowired
    private TestRestTemplate restTemplate;

    private List<Music> defaultSongs = new ArrayList<>() {
        {
            add(new Music("Sweet Dreams", "Beyoncé", 2008, "Pop, R&B",
                    "A pop-R&B track with catchy synths and Beyoncé's powerful vocals, exploring themes of love and longing.",
                    230, 120, 85, 90));
            add(new Music("Nothing New", "Charlotte Day Wilson", 2021, "R&B, Soul",
                    "A reflective, soulful track with emotional lyrics and smooth instrumentation.", 210, 85, 60, 70));
            add(new Music("Wuthering Heights", "Kate Bush", 1978, "Art Rock",
                    "A dramatic and ethereal track with Kate Bush's unique vocals and complex instrumentation.", 240,
                    130, 85, 70));
        }
    };

    @MockBean
    private MusicService musicService;

    @BeforeEach
    void setUp() throws RuntimeException {
        this.baseURI = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("music")
                .build()
                .toUri();

        when(musicService.getAllSongs()).thenReturn(defaultSongs);
    }

    @Test
    @Description("POST /music creates new Song")
    void createSong() {
        // Arrange: sending the http POST request and receive a response
        Music music = createNewSong();

        when(musicService.createSong(any(Music.class))).thenAnswer(invocation -> setId(invocation.getArgument(0)));

        // Act
        ResponseEntity<Music> response = restTemplate.postForEntity(baseURI.toString(), music, Music.class);

        // Assert
        // checks that the status code is 201
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // check that the response is not null
        assertNotNull(response.getBody());
        // check that the response id is not null
        assertNotNull(response.getBody().getId());
        // check that the createSong was implemented
        verify(musicService).createSong(any(Music.class));
    }

    @Test
    @Description("GET /music returns all songs")
    void getAllSongs() throws URISyntaxException {
        // Act: sending the http get request and receive a response
        ResponseEntity<List<Music>> response = restTemplate.exchange(baseURI, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Music>>() {
                });
        List<Music> responseSongs = response.getBody();

        // Assert
        // check that the status code is 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // check that the response is not null
        assertNotNull(responseSongs);
        // check that the size of the defaultSongs matches the responseSongs size
        assertEquals(defaultSongs.size(), responseSongs.size());
        // check that the getAllSongs was implemented
        verify(musicService).getAllSongs();

    }

    @Test
    @Description("GET /music/{id} returns id associated song")
    void getSong() {
        // Arrange
        // select random default song
        Music music = selectRandomSong();
        // add getSong route to the base URL
        URI endpoint = getEndpoint(music);
        // imitate database behaviour, retun song
        when(musicService.getSong(any(UUID.class))).thenReturn(music);

        // Act
        // send GET request to getSong route and stores response
        ResponseEntity<Music> response = restTemplate.getForEntity(endpoint, Music.class);

        // Assert
        // check that the status code is 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // check that the response is not null
        assertNotNull(response.getBody());
        // check that the response id matches the Music instance id
        assertEquals(music.getId(), response.getBody().getId());
        // check that getSong was implemented
        verify(musicService).getSong(music.getId());
    }

    @Test
    @Description("GET /music/{id} returns 404 for invalid Song")
    void getInvalidSong() {
        // Arrange
        Music music = createNewSong();
        URI endpoint = getEndpoint(music);

        // imitate database behaviour when song is not found, NoSuchElementException is
        // thrown
        when(musicService.getSong(any(UUID.class))).thenThrow(new NoSuchElementException("Song not found"));

        // Act
        ResponseEntity<Music> response = restTemplate.getForEntity(endpoint, Music.class);

        // Assert
        // check that the status code is 404
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // check that getSong was implemented
        verify(musicService).getSong(music.getId());
    }

    @Test
    @Description("PUT /music/{id} updates selected song")
    void updateSong() {
        // Arrange
        Music music = selectRandomSong();
        URI endpoint = getEndpoint(music);

        // imitate database behaviour when song is updated by returning a song when
        // getSong and updateSong are called
        when(musicService.getSong(any(UUID.class))).thenReturn(music);
        when(musicService.updateSong(any(UUID.class), any(Music.class))).thenReturn(music);

        // Act
        // update song name
        music.setSong("UpdatedSong");
        // send PUT request with updated music object
        restTemplate.put(endpoint, music);

        // sends get request to obtain updated music object
        ResponseEntity<Music> response = restTemplate.getForEntity(endpoint, Music.class);
        Music updatedSong = response.getBody();

        // Assert
        // check that the status code is 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // check that the response id matches the Music instance id
        assertEquals(music.getId(), updatedSong.getId());
        // check that the song parameter is updatedSong
        assertEquals("UpdatedSong", updatedSong.getSong());
        // check that getSong was implemented
        verify(musicService).getSong(music.getId());
        // check that updateSong was implemented
        verify(musicService).updateSong(any(UUID.class), any(Music.class));
    }

    @Test
    @Description("PUT /music/{id} returns 404 for invalid Song")
    void updateInvalidSong() {
        // Arrange
        Music music = createNewSong();
        URI endpoint = getEndpoint(music);
        // imitate database behaviour when song to be updated is not found,
        // NoSuchElementException thrown
        when(musicService.updateSong(any(UUID.class), any(Music.class)))
                .thenThrow(NoSuchElementException.class);

        // Act
        // send PUT request with music object as body
        RequestEntity<Music> request = RequestEntity.put(endpoint).accept(MediaType.APPLICATION_JSON).body(music);
        ResponseEntity<Music> response = restTemplate.exchange(request, Music.class);

        // Assert
        // check that the status code is 404
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // check that updateSong was implemented
        verify(musicService).updateSong(any(UUID.class), any(Music.class));
    }

    @Test
    @Description("DELETE /music/{id} deletes selected song")
    void deleteSong() {
        // Arrange
        Music music = selectRandomSong();
        URI endpoint = getEndpoint(music);
        // imitate database obtaining data for song specified by endpoint
        when(musicService.getSong(any(UUID.class))).thenReturn(music);
        // send get request to check for song existence
        ResponseEntity<Music> foundResponse = restTemplate.getForEntity(endpoint, Music.class);

        // imitates deletion by returning null
        doAnswer(invocation -> {
            return null;
        }).when(musicService).deleteSong(any(UUID.class));
        // throw NoSuchElementException after deleting song
        when(musicService.getSong(any(UUID.class))).thenThrow(NoSuchElementException.class);

        // Act
        // delete request at specified endoint
        RequestEntity<?> request = RequestEntity.delete(endpoint).accept(MediaType.APPLICATION_JSON).build();
        // storing response to deletion request
        ResponseEntity<?> deletionResponse = restTemplate.exchange(request, Object.class);
        // sends GET request to check song deletion
        ResponseEntity<Music> deletedResponse = restTemplate.getForEntity(endpoint, Music.class);

        // Assert
        // checks that initial GET request results in status 200
        assertEquals(HttpStatus.OK, foundResponse.getStatusCode());
        // check that after deletion the status code is 200 or 204
        assertTrue(deletionResponse.getStatusCode() == HttpStatus.OK
                || deletionResponse.getStatusCode() == HttpStatus.NO_CONTENT);
        assertEquals(HttpStatus.NOT_FOUND, deletedResponse.getStatusCode());
        // checks that deleteSong was implemented
        verify(musicService).deleteSong(music.getId());
    }

    @Test
    @Description("DELETE /music/{id} returns 404 for invalid song")
    void deleteInvalidSong() {
        // Arrange
        Music music = createNewSong();
        URI endpoint = getEndpoint(music);
        // emulate deleting non existent song from database
        doThrow(NoSuchElementException.class).when(musicService).deleteSong(any(UUID.class));

        // Act
        // delete request to specified endpoint
        RequestEntity<?> request = RequestEntity.delete(endpoint).accept(MediaType.APPLICATION_JSON).build();
        // store deletion request response (404 not found)
        ResponseEntity<Music> response = restTemplate.exchange(request, Music.class);

        // Assert
        // check status code is 404
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // check deleteSong was implemented
        verify(musicService).deleteSong(music.getId());
    }

    @Test
    @Description("GET /music/highdanceability returns high danceability songs")
    void getHighDanceabilitySongs() {
        // Arrange

        // imitate database behaviour to return high danceability songs
        when(musicService.getHighDanceabilitySongs()).thenReturn(highDanceabilitySongs);
        URI endpoint = getCustomEndpoint("highdanceability");

        // Act
        // send GET request to highdanceability route and stores response
        ResponseEntity<List<Music>> response = restTemplate.exchange(endpoint, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Music>>() {
                });

        // Assert
        // check that the status code is 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // check that the response is not null
        assertNotNull(response.getBody());
        // check that the response size matches the highDanceabilitySongs size
        assertEquals(highDanceabilitySongs.size(), response.getBody().size());
        // check that getHighDanceabilitySongs was implemented
        verify(musicService).getHighDanceabilitySongs();
    }

    @Test
    @Description("GET /music/lowenergy returns low energy songs")
    void getLowEnergySongs() {
        // Arrange

        // imitate database behaviour to return low energy songs
        when(musicService.getLowEnergySongs()).thenReturn(lowEnergySongs);
        URI endpoint = getCustomEndpoint("lowenergy");

        // Act
        // send GET request to lowenergy route and stores response
        ResponseEntity<List<Music>> response = restTemplate.exchange(endpoint, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Music>>() {
                });

        // Assert
        // check that the status code is 200
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // check that the response is not null
        assertNotNull(response.getBody());
        // check that the response size matches the lowEnergySongs size
        assertEquals(lowEnergySongs.size(), response.getBody().size());
        // check that getLowEnergySongs was implemented
        verify(musicService).getLowEnergySongs();
    }

    // create mock data for gethighDanceabilitySongs() test
    private List<Music> highDanceabilitySongs = new ArrayList<>() {
        {
            add(new Music("Sweet Dreams", "Beyoncé", 2008, "Pop, R&B",
                    "A pop-R&B track with catchy synths and Beyoncé's powerful vocals, exploring themes of love and longing.",
                    230, 120, 85, 90));
            add(new Music("Nothing New", "Charlotte Day Wilson", 2021, "R&B, Soul",
                    "A reflective, soulful track with emotional lyrics and smooth instrumentation.", 210, 85, 60, 70));
            add(new Music("Wuthering Heights", "Kate Bush", 1978, "Art Rock",
                    "A dramatic and ethereal track with Kate Bush's unique vocals and complex instrumentation.", 240,
                    130, 85, 70));
        }
    };

    // create mock data for getLowEnergySongs() test
    private List<Music> lowEnergySongs = new ArrayList<>() {
        {
            add(new Music("Nothing New", "Charlotte Day Wilson", 2021, "R&B, Soul",
                    "A reflective, soulful track with emotional lyrics and smooth instrumentation.", 210, 85, 60, 70));
            add(new Music("Sweet Dreams", "Beyoncé", 2008, "Pop, R&B",
                    "A pop-R&B track with catchy synths and Beyoncé's powerful vocals, exploring themes of love and longing.",
                    230, 120, 85, 90));
            add(new Music("Wuthering Heights", "Kate Bush", 1978, "Art Rock",
                    "A dramatic and ethereal track with Kate Bush's unique vocals and complex instrumentation.", 240,
                    130, 85, 70));
        }
    };

    // create mock data
    private Music createNewSong() {
        return setId(new Music("Sweet Dreams", "Beyoncé", 2008, "Pop, R&B",
                "A pop-R&B track with catchy synths and Beyoncé's powerful vocals, exploring themes of love and longing.",
                230, 120, 85, 90));
    }

    private static Music setId(Music music) {
        // creates random UUID
        ReflectionTestUtils.setField(music, "id", UUID.randomUUID());
        return music;
    }

    private Music selectRandomSong() {
        // allocate random id to randomly chosen default song
        int randomIndex = new Random().nextInt(defaultSongs.size());

        return setId(defaultSongs.get(randomIndex));
    }

    private URI getCustomEndpoint(String endpoint) {
        // create endpoint with custom endpoint
        return appendPath(baseURI, endpoint);
    }

    private URI getEndpoint(Music music) {
        // create endpoint with song id
        return appendPath(baseURI, music.getId().toString());
    }

    private URI appendPath(URI uri, String path) {
        // append end point to the base URI
        return UriComponentsBuilder.fromUri(uri).pathSegment(path).build().encode().toUri();
    }

}
