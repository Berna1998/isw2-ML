package main.java.ml.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import main.java.ml.model.Ticket;
import main.java.ml.model.Version;

public class JiraExtractor {
	private String projName;

	public JiraExtractor(String name) {
		this.projName = name;
	}

	public List<Version> getVersions() throws IOException, ParseException {

		ArrayList<Version> versionsList = new ArrayList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;

		JSONObject json = readJsonFromUrl(url);
		JSONArray versions = json.getJSONArray("versions");
		int total = versions.length();
		int i = 0;
		for (i = 0; i < total; i++) {
			if (versions.getJSONObject(i).has("releaseDate")) {
				String name = versions.getJSONObject(i).get("name").toString();
				String releaseDateS = versions.getJSONObject(i).get("releaseDate").toString();
				Date releaseDate = formatter.parse(releaseDateS);

				Version v = new Version(name, releaseDate, 0);
				versionsList.add(v);

			}
		}

		versionsList.sort(Comparator.comparing(Version::getDate));

		for (i = 1; i < versionsList.size() + 1; i++) {
			versionsList.get(i - 1).setIndex(i);
		}

		return versionsList;
	}

	public List<Ticket> getIssues(List<Version> versions) throws IOException, ParseException {
		List<Ticket> ticketList = new ArrayList<>();
		Integer s = 0;
		Integer j = 0;
		Integer total = 1;

		do {
			j = s + 1000;

			String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22" + projName
					+ "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
					+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,fixVersions,resolutiondate,versions,created&startAt="
					+ s.toString() + "&maxResults=" + j.toString();
			JSONObject json = readJsonFromUrl(url);
			JSONArray issues = json.getJSONArray("issues");
			total = json.getInt("total");
			// VEDI
			for (; s < total && s < j; s++) {
				Ticket tick = createTicket(issues, s, versions);

				if (tick != null) {
					ticketList.add(tick);
				}
			}

		} while (s < total);

		ticketList.sort(Comparator.comparing(Ticket::getResolutionDate));
		return ticketList;
	}

	private static Ticket createTicket(JSONArray issues, Integer index, List<Version> versions) throws ParseException {
		Ticket ticket = null;
		JSONObject jsonIssue = issues.getJSONObject(index % 1000);
		JSONObject fields = jsonIssue.getJSONObject("fields");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String key = jsonIssue.get("key").toString();
		String resDateStr = fields.get("resolutiondate").toString();
		String creDateStr = fields.get("created").toString();

		Date resDate = formatter.parse(resDateStr);
		Date creDate = formatter.parse(creDateStr);

		JSONArray avListInit = fields.getJSONArray("versions");

		ArrayList<Version> affectedVerList = new ArrayList<>();

		Version openVersion = getFromDate(creDate, versions);
		Version fixVersion = getFromDate(resDate, versions);

		int k;
		for (k = 0; k < avListInit.length(); k++) {
			Version affectedV = getAffectedName(avListInit.getJSONObject(k).get("name").toString(), versions);
			if (affectedV != null) {
				affectedVerList.add(affectedV);
			}
		}
		affectedVerList.sort(Comparator.comparing(Version::getDate));

		Version iv = null;
		Ticket tempTick = new Ticket(key, iv, fixVersion, openVersion, affectedVerList, resDate);
		if (!affectedVerList.isEmpty()) {
			if (checkTicket(tempTick)) {
				ticket = orderTicket(tempTick, versions);
			}
		} else {
			if (tempTick.getOv() != null && tempTick.getFv() != null) {
				// && openingVersion.id()!=releasesList.get(0).id()
				return tempTick;
			}
		}

		return ticket;
	}

	private static boolean checkTicket(Ticket tick) {

		// Controllo se alcuni dati mancano o non sono congrui
		if (tick.getFv() == null || tick.getOv() == null || tick.getOv().getDate().after(tick.getFv().getDate())
				|| !(tick.getAv().get(0).getDate().before(tick.getOv().getDate()))) {
			return false;
		}

		// Controllo se ci sono Release più grandi della FixVersion
		for (int i = 0; i < tick.getAv().size(); i++) {

			if (tick.getAv().get(i).getIndex() > tick.getFv().getIndex()) {
				return false;
			}

		}

		return true;
	}

	private static Ticket orderTicket(Ticket ticket, List<Version> vers) {

		int i;
		List<Version> newAv = new ArrayList<>();

		ticket.setIv(ticket.getAv().get(0));
		for (i = ticket.getIv().getIndex(); i < ticket.getFv().getIndex(); i++) {
			for (Version ver : vers) {
				if (ver.getIndex() == i) {
					newAv.add(new Version(ver.getName(), ver.getDate(), i));
				}
			}
		}

		ticket.setAv(newAv);
		return ticket;
	}

	private static Version getAffectedName(String affectedName, List<Version> versions) {
		Version v = null;
		int i;
		for (i = 0; i < versions.size(); i++) {
			if (affectedName.equals(versions.get(i).getName())) {
				v = versions.get(i);
			}
		}
		return v;
	}

	private static Version getFromDate(Date creDate, List<Version> versions) {
		Version v = null;
		int i;
		for (i = 0; i < versions.size(); i++) {

			if (!versions.get(i).getDate().before(creDate)) {
				return versions.get(i);
			}
		}
		return v;
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException {

		try (InputStream is = new URL(url).openStream()) {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String jsonText = readAll(rd);
			return new JSONObject(jsonText);
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();

	}

	public List<Ticket> adjustTicketForCold(List<Ticket> oldList) {
		List<Ticket> newList = new ArrayList<>();
		for (Ticket ticket : oldList) {
			if (!ticket.getAv().isEmpty()) {
				newList.add(ticket);
			}
		}
		newList.sort(Comparator.comparing(Ticket::getResolutionDate));
		return newList;
	}

}
