package apidiff.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import apidiff.cmp.Delta;
import apidiff.cmp.Delta.Status;
import apidiff.model.ElementInfo;
import apidiff.model.ElementTag;

public class HTMLRenderer {

	private IMultiReportOutput output;

	public HTMLRenderer(IMultiReportOutput output) {
		this.output = output;
	}

	public void render(Delta delta) throws IOException {
		copyResources();
		try (HTMLElement html = new HTMLElement(output.createFile("index.html"), "UTF-8")) {

			HTMLElement head = html.head();
			head.title().text(delta.getElement().getName());
			head.link("stylesheet", "style.css", "text/css");

			HTMLElement body = html.body();
			body.h1().text(delta.getElement().getName());

			HTMLElement tbody = body.table().tbody();
			for (Delta c : delta.getChildren()) {
				renderElement(tbody, c);
			}
		}
	}

	private void renderElement(HTMLElement tbody, Delta delta) throws IOException {
		HTMLElement tr = tbody.tr();
		ElementInfo element = delta.getElement();
		tr.td(element.getType().name().toLowerCase()).text(element.getName());
		renderTags(tr.td(), delta);
		for (Delta c : delta.getChildren()) {
			renderElement(tbody, c);
		}
	}

	private void renderTags(HTMLElement td, Delta delta) throws IOException {
		if (Status.ADDED.equals(delta.getStatus())) {
			HTMLElement span = td.span("added");
			span.text("added");
		}
		if (Status.REMOVED.equals(delta.getStatus())) {
			HTMLElement span = td.span("removed");
			span.text("removed");
		}
		for (ElementTag tag : delta.getAddedTags()) {
			HTMLElement span = td.span("tagadd");
			span.b().text("+");
			span.text(" ");
			span.text(tag.toString());
		}
		for (ElementTag tag : delta.getRemovedTags()) {
			HTMLElement span = td.span("tagremove");
			span.b().text("-");
			span.text(" ");
			span.text(tag.toString());
		}
	}

	private void copyResources() throws IOException {
		copyResource("style.css");
		copyResource("annotation.png");
		copyResource("class.png");
		copyResource("enum.png");
		copyResource("field.png");
		copyResource("interface.png");
		copyResource("method.png");
		copyResource("package.png");
	}

	private void copyResource(String name) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(name); OutputStream out = output.createFile(name)) {
			in.transferTo(out);
		}
	}

}
