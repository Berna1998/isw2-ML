package main.java.ml.extractor;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.java.ml.model.*;
import main.java.ml.model.Ticket;
import main.java.ml.model.Version;

public class ProportionControl {
	private List<String> projectsForCold = new ArrayList<>(List.of("ZOOKEEPER", "SYNCOPE", "TAJO", "AVRO", "OPENJPA"));
	private List<List<Ticket>> allListsTickets = new ArrayList<>();

	public ProportionControl() throws IOException, ParseException {
		for (String name : projectsForCold) {
			JiraExtractor je = new JiraExtractor(name);
			List<Version> versionsList = je.getVersions();
			List<Ticket> ticketsList = je.getIssues(versionsList);
			List<Ticket> goodList = je.adjustTicketForCold(ticketsList);
			allListsTickets.add(goodList);
		}
	}

	int cold = 0;
	int increm = 0;

	public List<Ticket> doProportion(List<Ticket> tickList, List<Version> versions) {
		List<Ticket> ticketFinalLis = new ArrayList<>();
		List<Ticket> goodTickets = new ArrayList<>();

		double proportion;
		for (Ticket tick : tickList) {
			if (tick.getAv().isEmpty()) {
				proportion = calculateProportion(goodTickets);
				useProportionForTicket(proportion, tick, versions);
				completeTicket(tick, versions);
			} else {
				// MEttilo tra i ticket da passare
				goodTickets.add(tick);
			}

			ticketFinalLis.add(tick);
		}

		ticketFinalLis.sort(Comparator.comparing(Ticket::getResolutionDate));

		return ticketFinalLis;
	}

	private double calculateProportion(List<Ticket> tickList) {
		double proportion;
		if (tickList.size() >= 5) {
			// Incremental
			increm++;
			proportion = incremental(tickList);
		} else {
			// Cold Start
			cold++;
			proportion = coldStart();
		}
		return proportion;
	}

	private double incremental(List<Ticket> tickList) {
		double mean;
		double totalP = 0.0;
		for (Ticket tick : tickList) {
			double iv = tick.getIv().getIndex();
			double p;
			double fv = tick.getFv().getIndex();
			double ov = tick.getOv().getIndex();
			if (fv != ov) {
				p = (fv - iv) / (fv - ov);
			} else {
				p = fv - iv;
			}
			totalP += p;
		}
		mean = totalP / tickList.size();
		return mean;
	}

	private double coldStart() {
		double value;
		List<Double> pList = new ArrayList<>();
		for (List<Ticket> list : allListsTickets) {
			if (list.size() >= 5) {
				pList.add(incremental(list));
			}
		}
		Collections.sort(pList);
		int size = pList.size();

		if (size % 2 == 0) {
			value = (pList.get(size / 2 - 1) + pList.get(size / 2)) / 2.0;
		} else {
			value = pList.get(size / 2);
		}

		return value;
	}

	private void useProportionForTicket(Double proportion, Ticket ticket, List<Version> versions) {
		List<Version> avInit = new ArrayList<>();
		int indexIV = (int) (ticket.getFv().getIndex()
				- (ticket.getFv().getIndex() - ticket.getOv().getIndex()) * proportion);
		if (indexIV < 1) {
			indexIV = 1;
		}

		for (Version ver : versions) {
			if (ver.getIndex() == indexIV) {
				avInit.add(new Version(ver.getName(), ver.getDate(), indexIV));
			}
		}

		ticket.setIv(avInit.get(0));
		ticket.setAv(avInit);

	}

	private void completeTicket(Ticket ticket, List<Version> versions) {
		int i;
		List<Version> newAv = new ArrayList<>();

		for (i = ticket.getIv().getIndex(); i < ticket.getFv().getIndex(); i++) {
			for (Version ver : versions) {
				if (ver.getIndex() == i) {
					newAv.add(new Version(ver.getName(), ver.getDate(), i));
				}
			}
		}
		ticket.setAv(newAv);
	}
}
