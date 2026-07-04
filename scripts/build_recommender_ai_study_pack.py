#!/usr/bin/env python3
from __future__ import annotations

import json
import os
import re
import shutil
import subprocess
import tempfile
from pathlib import Path

import fitz


WORKSPACE_ROOT = Path(__file__).resolve().parents[1]
PACK_ROOT = WORKSPACE_ROOT / "ai-study-pack"
HEAVY_ROOT = PACK_ROOT / "heavy"
SECTION_BUNDLES = HEAVY_ROOT / "section-bundles"
SOURCE_EXTRACTS = HEAVY_ROOT / "source-extracts"
NOTES_OUT = SOURCE_EXTRACTS / "notes"
SLIDES_OUT = SOURCE_EXTRACTS / "slides"

OFFICIAL_REPO = "https://github.com/wangshusen/RecommenderSystem.git"
USER_PLAYLIST_ROOT = "https://www.bilibili.com/video/BV1FwXrBmEp4"

SECTION_SLUGS = {
    1: "overview",
    2: "retrieval",
    3: "ranking",
    4: "cross-features",
    5: "sequence-modeling",
    6: "diversity",
    7: "cold-start",
    8: "improvement",
}

SECTION_GUIDANCE = {
    1: {
        "focus": [
            "Understand why recommender systems optimize business metrics instead of only click metrics.",
            "Understand the full industrial pipeline: retrieval, ranking, reranking, and A/B experimentation.",
            "Understand the difference between core metrics and supporting metrics.",
        ],
        "pitfalls": [
            "Do not reduce the course to CTR optimization.",
            "Do not treat A/B testing as an afterthought.",
        ],
    },
    2: {
        "focus": [
            "Learn retrieval as a multi-channel system instead of a single model.",
            "Understand the tradeoffs among ItemCF, Swing, UserCF, two-tower, and Deep Retrieval.",
            "Understand why online indexing and filtering are part of retrieval design.",
        ],
        "pitfalls": [
            "Do not assume a single retrieval model is enough.",
            "Do not forget hard negatives, ANN indexing, and exposure filtering.",
        ],
    },
    3: {
        "focus": [
            "Learn ranking as a multi-objective optimization problem.",
            "Understand score fusion, task conflict, and why watch-time matters in video products.",
            "Understand the role difference between coarse rank and fine rank.",
        ],
        "pitfalls": [
            "Do not optimize only CTR.",
            "Do not confuse ranking model structure with business value definition.",
        ],
    },
    4: {
        "focus": [
            "Understand why feature interaction is central in sparse recommendation settings.",
            "Learn the roles of FM, DCN, LHUC, SENet, and FiBiNET.",
        ],
        "pitfalls": [
            "Do not assume a plain MLP can replace explicit or structured feature crosses in every case.",
        ],
    },
    5: {
        "focus": [
            "Understand why recent user behavior often captures current intent better than static profile features.",
            "Learn the motivation and tradeoff between DIN and SIM.",
        ],
        "pitfalls": [
            "Do not ignore sequence length and compute cost.",
            "Do not lose long-term interest while modeling short-term intent.",
        ],
    },
    6: {
        "focus": [
            "Treat diversity as a retention and satisfaction problem, not as a cosmetic problem.",
            "Learn both simple and advanced approaches: rules, MMR, and DPP.",
        ],
        "pitfalls": [
            "Do not assume high relevance alone produces healthy long-term feeds.",
        ],
    },
    7: {
        "focus": [
            "Understand cold start as both a user-consumption problem and a creator-ecosystem problem.",
            "Learn the roles of content pools, clustering, look-alike expansion, and traffic control.",
        ],
        "pitfalls": [
            "Do not evaluate cold start only with user-side metrics.",
            "Do not assume ordinary A/B design is enough for creator-side effects.",
        ],
    },
    8: {
        "focus": [
            "Study how industrial teams actually improve recommender metrics over time.",
            "Learn how retrieval, ranking, diversity, special populations, and interaction behaviors combine into growth strategy.",
        ],
        "pitfalls": [
            "Do not think growth comes only from deeper models.",
            "Do not ignore special-user strategies and high-value interactions.",
        ],
    },
}

MISSING_FROM_USER_PLAYLIST = {
    "Multi-gate Mixture-of-Experts (MMoE)",
    "多样性的度量",
    "聚类召回",
}


def is_pdf_url(url: str | None) -> bool:
    return bool(url) and url.lower().endswith(".pdf")


def run(command: list[str], cwd: Path | None = None) -> None:
    subprocess.run(command, cwd=str(cwd) if cwd else None, check=True)


def clone_official_repo() -> Path:
    temp_root = Path(tempfile.mkdtemp(prefix="codex-recommender-course-source-"))
    run(["git", "clone", "--depth", "1", OFFICIAL_REPO, str(temp_root)])
    return temp_root


def normalize_text_block(text: str) -> str:
    lines: list[str] = []
    previous = None
    blank_pending = False
    for raw_line in text.splitlines():
        line = raw_line.replace("\xa0", " ").strip()
        line = re.sub(r"\s+", " ", line)
        if not line:
            if lines and not blank_pending:
                lines.append("")
                blank_pending = True
            previous = None
            continue
        if re.fullmatch(r"\d+", line):
            continue
        if line == previous:
            continue
        lines.append(line)
        previous = line
        blank_pending = False
    while lines and lines[-1] == "":
        lines.pop()
    return "\n".join(lines).strip()


def extract_pdf_markdown(pdf_path: Path, title: str, source_url: str) -> str:
    doc = fitz.open(pdf_path)
    parts = [f"# {title}", "", f"Source: {source_url}", ""]
    for page_index, page in enumerate(doc, start=1):
        text = normalize_text_block(page.get_text("text"))
        parts.append(f"## Page {page_index}")
        parts.append("")
        parts.append(text or "[No extractable text on this page]")
        parts.append("")
    return "\n".join(parts).strip() + "\n"


def parse_official_readme(readme_path: Path) -> list[dict]:
    text = readme_path.read_text(encoding="utf-8")
    lines = text.splitlines()
    sections: list[dict] = []
    current = None
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        section_match = re.match(
            r"^(\d+)\.\s+\*\*(.+?)\*\*(?:\s+\[\[(.+?)\]\((.+?)\)\])?",
            line,
        )
        if section_match:
            current = {
                "index": int(section_match.group(1)),
                "section": section_match.group(2),
                "note_label": section_match.group(3),
                "note_url": section_match.group(4),
                "items": [],
            }
            sections.append(current)
            i += 1
            continue

        item_match = re.match(r"^\*\s+(.+?)\s*$", line)
        if item_match and current is not None:
            topic = {"title": item_match.group(1)}
            j = i + 1
            while j < len(lines):
                candidate = lines[j].strip()
                if not candidate:
                    j += 1
                    continue
                if candidate.startswith("* ") or re.match(r"^\d+\.\s+\*\*", candidate):
                    break
                link_pairs = re.findall(r"\[\[(.+?)\]\((.+?)\)\]", candidate)
                if not link_pairs:
                    break
                for label, url in link_pairs:
                    topic[label.lower()] = url
                j += 1
            current["items"].append(topic)
        i += 1
    return sections


def build_manifest(sections: list[dict]) -> list[dict]:
    manifest: list[dict] = []
    official_order = 0
    playlist_part = 0
    for section in sections:
        for topic_index, item in enumerate(section["items"], start=1):
            official_order += 1
            in_playlist = item["title"] not in MISSING_FROM_USER_PLAYLIST
            if in_playlist:
                playlist_part += 1
            manifest.append(
                {
                    "official_order": official_order,
                    "section_index": section["index"],
                    "section_title": section["section"],
                    "topic_index_in_section": topic_index,
                    "title": item["title"],
                    "slides_url": item.get("slides"),
                    "bilibili_url": item.get("b站"),
                    "youtube_url": item.get("youtube"),
                    "note_url": section.get("note_url") if is_pdf_url(section.get("note_url")) else None,
                    "section_reference_url": section.get("note_url") if not is_pdf_url(section.get("note_url")) else None,
                    "in_user_playlist": in_playlist,
                    "user_playlist_part": playlist_part if in_playlist else None,
                }
            )
    return manifest


def write_text(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")


def build_playlist_gap_analysis(manifest: list[dict]) -> str:
    missing_rows = [row for row in manifest if not row["in_user_playlist"]]
    lines = [
        "# Playlist Gap Analysis",
        "",
        f"User playlist root: {USER_PLAYLIST_ROOT}",
        f"Official course source: {OFFICIAL_REPO.removesuffix('.git')}",
        "",
        f"- Official topic count: `{len(manifest)}`",
        f"- User playlist topic count: `{sum(1 for row in manifest if row['in_user_playlist'])}`",
        f"- Missing official topics from the user's playlist: `{len(missing_rows)}`",
        "",
        "## Missing Topics",
        "",
    ]
    for row in missing_rows:
        lines.append(
            f"- Official #{row['official_order']}: `{row['title']}` "
            f"(section `{row['section_title']}`)"
        )
    lines.extend(["", "## Official To Playlist Mapping", ""])
    lines.append("| Official # | Section | Topic | In 42-part Playlist | Playlist Part |")
    lines.append("|---|---|---|---|---|")
    for row in manifest:
        lines.append(
            "| {official_order} | {section_title} | {title} | {in_user_playlist} | {user_playlist_part} |".format(
                official_order=row["official_order"],
                section_title=row["section_title"],
                title=row["title"].replace("|", "/"),
                in_user_playlist="yes" if row["in_user_playlist"] else "no",
                user_playlist_part=row["user_playlist_part"] or "-",
            )
        )
    lines.extend(
        [
            "",
            "## Interpretation",
            "",
            "- The 42-part playlist is a very strong coverage set, but it is not the complete official syllabus.",
            "- Another AI that studies only the 42-part playlist may miss one ranking topic, one diversity topic, and one cold-start topic.",
            "- For full mastery, the AI should study the full official 45-topic syllabus.",
        ]
    )
    return "\n".join(lines) + "\n"


def build_heavy_readme() -> str:
    return "\n".join(
        [
            "# Heavy AI Study Pack",
            "",
            "This folder is the heavyweight version of the recommender-course study pack.",
            "",
            "It is designed for another AI that needs more than a quick summary. It contains:",
            "",
            "- `full-course-dossier.md`: one heavy, consolidated document for one-shot ingestion.",
            "- `manifest.json`: machine-readable official syllabus metadata.",
            "- `playlist-gap-analysis.md`: exact mapping between the official 45-topic syllabus and the user's 42-part playlist.",
            "- `section-bundles/`: 8 chapter bundles that combine source links, study guidance, and inline extracted source text.",
            "- `source-extracts/`: page-by-page extracted text from the official notes and slide PDFs.",
            "",
            "Recommended ingestion order for another AI:",
            "",
            "1. Read `../project-summary.md`",
            "2. Read `../detailed-course-notes.md`",
            "3. Read `../algorithm-index.md`",
            "4. Read `full-course-dossier.md`",
            "5. If deeper evidence is needed, read the matching file under `section-bundles/`",
            "6. Use `../mastery-checklist.md` to verify understanding",
            "",
            "Important limitation:",
            "",
            "- This pack gets much closer to full mastery than a simple summary.",
            "- It still cannot mathematically guarantee perfect reproduction of every spoken nuance from the videos.",
            "- It *does* provide structured explanations plus direct local source extracts from the official materials, which is the best reliable path in this repo.",
        ]
    ) + "\n"


def section_bundle_name(section_index: int) -> str:
    return f"{section_index:02d}-{SECTION_SLUGS[section_index]}-bundle.md"


def build_section_bundle(
    source_root: Path,
    section: dict,
    section_rows: list[dict],
) -> str:
    guidance = SECTION_GUIDANCE.get(section["index"], {"focus": [], "pitfalls": []})
    lines = [
        f"# Section {section['index']}: {section['section']}",
        "",
        "## Why This Section Matters",
        "",
    ]
    for point in guidance["focus"]:
        lines.append(f"- {point}")
    if guidance["pitfalls"]:
        lines.extend(["", "## Common Mistakes To Avoid", ""])
        for point in guidance["pitfalls"]:
            lines.append(f"- {point}")

    lines.extend(["", "## Topic List", ""])
    for row in section_rows:
        playlist_info = (
            f"user playlist part `{row['user_playlist_part']}`"
            if row["in_user_playlist"]
            else "missing from the user's 42-part playlist"
        )
        lines.append(f"- Official #{row['official_order']}: `{row['title']}` ({playlist_info})")

    lines.extend(["", "## Official Source Links", ""])
    if is_pdf_url(section.get("note_url")):
        lines.append(f"- Section note: {section['note_url']}")
    elif section.get("note_url"):
        lines.append(f"- Section reference: {section['note_url']}")
    for item in section["items"]:
        lines.append(
            f"- `{item['title']}`: slides `{item.get('slides', '-')}`, "
            f"Bilibili `{item.get('b站', '-')}`, YouTube `{item.get('youtube', '-')}`"
        )

    lines.extend(["", "## Local Extract Files", ""])
    if is_pdf_url(section.get("note_url")):
        note_name = Path(section["note_url"]).name.replace(".pdf", ".md")
        lines.append(f"- Note extract: `../source-extracts/notes/{note_name}`")
    for item in section["items"]:
        if item.get("slides"):
            slide_name = Path(item["slides"]).name.replace(".pdf", ".md")
            lines.append(f"- `{item['title']}`: `../source-extracts/slides/{slide_name}`")

    if is_pdf_url(section.get("note_url")):
        note_pdf = source_root / "Notes" / Path(section["note_url"]).name
        note_md = extract_pdf_markdown(
            note_pdf,
            title=f"{section['section']} - Official Note",
            source_url=section["note_url"],
        )
        lines.extend(["", "## Inline Note Extract", "", note_md.strip(), ""])

    lines.extend(["## Inline Slide Extracts", ""])
    for item in section["items"]:
        if not item.get("slides"):
            continue
        slide_pdf = source_root / "Slides" / Path(item["slides"]).name
        slide_md = extract_pdf_markdown(
            slide_pdf,
            title=item["title"],
            source_url=item["slides"],
        )
        lines.extend([f"### {item['title']}", "", slide_md.strip(), ""])

    return "\n".join(lines).strip() + "\n"


def build_full_course_dossier(sections: list[dict], manifest: list[dict]) -> str:
    project_summary = (PACK_ROOT / "project-summary.md").read_text(encoding="utf-8")
    detailed_notes = (PACK_ROOT / "detailed-course-notes.md").read_text(encoding="utf-8")
    algorithm_index = (PACK_ROOT / "algorithm-index.md").read_text(encoding="utf-8")
    checklist = (PACK_ROOT / "mastery-checklist.md").read_text(encoding="utf-8")

    lines = [
        "# Full Course Dossier",
        "",
        "This is the heavyweight, single-file dossier for another AI.",
        "",
        "Primary source basis:",
        "",
        f"- Official course repo: {OFFICIAL_REPO.removesuffix('.git')}",
        f"- User playlist root: {USER_PLAYLIST_ROOT}",
        "",
        "## Course Coverage Facts",
        "",
        f"- Official topic count: `{len(manifest)}`",
        f"- User playlist topic count: `{sum(1 for row in manifest if row['in_user_playlist'])}`",
        f"- Missing official topics from the user's playlist: `{sum(1 for row in manifest if not row['in_user_playlist'])}`",
        "",
        "Missing official topics:",
        "",
    ]
    for row in manifest:
        if not row["in_user_playlist"]:
            lines.append(f"- `{row['title']}`")

    lines.extend(
        [
            "",
            "## Recommended Learning Order",
            "",
            "1. Internalize the project-oriented summary.",
            "2. Study the detailed course notes end to end.",
            "3. Memorize the algorithm index.",
            "4. Use the official topic map and playlist gap analysis for source navigation.",
            "5. Read section bundles when exact source grounding is needed.",
            "6. Use the mastery checklist as a self-test before claiming mastery.",
            "",
            "## Project Summary",
            "",
            project_summary.strip(),
            "",
            "## Detailed Course Notes",
            "",
            detailed_notes.strip(),
            "",
            "## Algorithm Index",
            "",
            algorithm_index.strip(),
            "",
            "## Official Topic Inventory",
            "",
            "| Official # | Section | Topic | Playlist Part |",
            "|---|---|---|---|",
        ]
    )
    for row in manifest:
        lines.append(
            "| {official_order} | {section_title} | {title} | {playlist} |".format(
                official_order=row["official_order"],
                section_title=row["section_title"],
                title=row["title"].replace("|", "/"),
                playlist=row["user_playlist_part"] if row["user_playlist_part"] else "missing",
            )
        )

    lines.extend(
        [
            "",
            "## Mastery Checklist",
            "",
            checklist.strip(),
            "",
            "## Heavy-Pack Navigation",
            "",
            "- Machine-readable syllabus: `manifest.json`",
            "- Official-to-playlist mapping: `playlist-gap-analysis.md`",
            "- Chapter bundles with inline extracts: `section-bundles/`",
            "- Raw extracted official texts: `source-extracts/`",
        ]
    )
    return "\n".join(lines).strip() + "\n"


def write_source_extracts(source_root: Path, sections: list[dict]) -> None:
    written_notes: set[str] = set()
    for section in sections:
        if is_pdf_url(section.get("note_url")):
            note_name = Path(section["note_url"]).name
            if note_name not in written_notes:
                note_pdf = source_root / "Notes" / note_name
                note_md = extract_pdf_markdown(
                    note_pdf,
                    title=note_name.replace(".pdf", ""),
                    source_url=section["note_url"],
                )
                write_text(NOTES_OUT / note_name.replace(".pdf", ".md"), note_md)
                written_notes.add(note_name)
        for item in section["items"]:
            if item.get("slides"):
                slide_name = Path(item["slides"]).name
                slide_pdf = source_root / "Slides" / slide_name
                slide_md = extract_pdf_markdown(
                    slide_pdf,
                    title=item["title"],
                    source_url=item["slides"],
                )
                write_text(SLIDES_OUT / slide_name.replace(".pdf", ".md"), slide_md)


def main() -> None:
    if HEAVY_ROOT.exists():
        shutil.rmtree(HEAVY_ROOT)
    HEAVY_ROOT.mkdir(parents=True, exist_ok=True)

    source_root = clone_official_repo()
    sections = parse_official_readme(source_root / "README.md")
    manifest = build_manifest(sections)

    write_source_extracts(source_root, sections)

    for section in sections:
        section_rows = [row for row in manifest if row["section_index"] == section["index"]]
        bundle = build_section_bundle(source_root, section, section_rows)
        write_text(SECTION_BUNDLES / section_bundle_name(section["index"]), bundle)

    write_text(HEAVY_ROOT / "README.md", build_heavy_readme())
    write_text(
        HEAVY_ROOT / "playlist-gap-analysis.md",
        build_playlist_gap_analysis(manifest),
    )
    write_text(
        HEAVY_ROOT / "full-course-dossier.md",
        build_full_course_dossier(sections, manifest),
    )
    (HEAVY_ROOT / "manifest.json").write_text(
        json.dumps(manifest, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
