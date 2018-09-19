package com.setheck.momentservice.resources;

import com.setheck.momentservice.api.*;
import com.setheck.momentservice.db.*;
import io.dropwizard.testing.junit.*;
import org.junit.*;
import org.junit.rules.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MomentResourceTest {
    private static final MomentDAO dao = mock(MomentDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new MomentResource(dao))
            .build();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final Moment moment = new Moment(1, "Some text", "Seth", false, new Date(0));

    @Before
    public void setup() {
        when(dao.findById(eq(1))).thenReturn(moment);
        when(dao.findById(0)).thenReturn(null);
        when(dao.updateSingleColumn(Collections.singleton(1), "approved", "true"))
                .thenReturn(1);

        Set<Integer> setOfThree = new HashSet<>(Arrays.asList(1, 2, 3));
        when(dao.updateSingleColumn(setOfThree, "approved", true))
                .thenReturn(3);
        when(dao.findRandomApproved()).thenReturn(moment);
        when(dao.findMomentsNeedingApproval()).thenReturn(Collections.singletonList(moment));
//        when(dao.delete(eq(1L))
    }

    @After
    public void tearDown() {
        // we have to reset the mock after each test because of the
        // @ClassRule.
        reset(dao);
    }

    @Test
    public void testGetMomentById(){
        assertThat(resources.client().target("/moment/1").request()
                .get(Moment.class))
                .isEqualTo(moment);

        verify(dao).findById(eq(1));
    }

    @Test
    public void testGetMomentByIdFindsNullMoment() {
        assertThat(resources.client().target("/moment/0").request()
                .get(Moment.class)).isEqualTo(null);
    }

    @Test
    public void testGetRandomApproved() {
        Moment randomMoment = resources.client().target("/moment/random").request()
                .get(Moment.class);
        //TODO: need integration test to verify only approved moments come back from db
        assertThat(randomMoment).isEqualTo(moment);
    }

    @Test
    public void testGetMomentsToApprove() {
        List<Moment> moments = resources.client().target("/moment/toapprove").request()
                .get(new GenericType<List<Moment>>(){});
        assertThat(moments).containsExactly(moment);
    }

    @Test
    public void testApproveByIdsSingleId() {

        verifyApproveUpdateRequest("1", Response.Status.OK.getStatusCode());

        verify(dao).updateSingleColumn(any(), eq("approved"), eq(true));
    }

    @Test
    public void testApproveByIdsMultipleIds() {
        verifyApproveUpdateRequest("1,2,3", Response.Status.OK.getStatusCode());

        verify(dao).updateSingleColumn(any(), eq("approved"), eq(true));
    }

    @Test
    public void testApproveByIdsEmptyIds() {
        verifyApproveUpdateRequest("", Response.Status.NOT_MODIFIED.getStatusCode());
        verify(dao, never()).updateSingleColumn(any(), any(), any());
    }

    private void verifyApproveUpdateRequest(String idsQueryParam, int expectedStatusCode) {
        Response response = resources.client().target("/moment/approve")
                .queryParam("ids", idsQueryParam)
                .request()
                .put(Entity.entity("", MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(expectedStatusCode);
    }
    @Test
    public void testAddNewMoment() {
        Response response = resources.client().target("/moment").request()
                .post(Entity.json(moment));

        verify(dao).create(moment);
        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void testAddNullMoment() {
        Response response = resources.client().target("/moment").request()
                .post(Entity.json(null));

        verify(dao, never()).create(any());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_MODIFIED.getStatusCode());
    }

    @Test
    public void testDeleteByIdMoment() {
        Response response = resources.client().target("/moment")
                .queryParam("id", "1")
                .request().delete();

        verify(dao).deleteById(1);
        assertThat(response.getStatus()).isEqualTo(Response.Status.ACCEPTED.getStatusCode());
    }

    @Test
    public void testDeleteByIdNull() {
        Response response = resources.client().target("/moment")
                .queryParam("id", "")
                .request().delete();

        verify(dao, never()).deleteById(any());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_MODIFIED.getStatusCode());
    }
}
