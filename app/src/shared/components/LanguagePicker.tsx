import { useState } from "react";
import {
  View, Text, TextInput, TouchableOpacity,
  ScrollView, StyleSheet, Modal,
} from "react-native";
import { LANGUAGES, Language } from "../lib/languages";

type Props = {
  value: string;
  onChange: (lang: Language) => void;
  searchPlaceholder?: string;
};

export default function LanguagePicker({ value, onChange, searchPlaceholder = "Search language..." }: Props) {
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState("");

  const selectedLang = LANGUAGES.find((l) => l.code === value);

  const filtered = LANGUAGES.filter(
    (l) =>
      l.name.toLowerCase().includes(search.toLowerCase()) ||
      l.nativeName.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <View>
      <TouchableOpacity style={styles.selector} onPress={() => setOpen(true)}>
        <Text style={styles.selectorText} numberOfLines={1}>
          {selectedLang
            ? `${selectedLang.nativeName}  —  ${selectedLang.name}`
            : "Select language"}
        </Text>
        <Text style={styles.arrow}>▾</Text>
      </TouchableOpacity>

      <Modal
        visible={open}
        transparent
        animationType="fade"
        onRequestClose={() => setOpen(false)}
      >
        <TouchableOpacity
          style={styles.overlay}
          activeOpacity={1}
          onPress={() => { setOpen(false); setSearch(""); }}
        >
          <TouchableOpacity activeOpacity={1} style={styles.dropdown}>
            <TextInput
              style={styles.searchInput}
              placeholder={searchPlaceholder}
              placeholderTextColor="#62666d"
              value={search}
              onChangeText={setSearch}
              autoFocus
            />
            <ScrollView style={styles.list} keyboardShouldPersistTaps="handled">
              {filtered.map((lang) => (
                <TouchableOpacity
                  key={lang.code}
                  style={[styles.item, lang.code === value && styles.itemActive]}
                  onPress={() => {
                    onChange(lang);
                    setSearch("");
                    setOpen(false);
                  }}
                >
                  <Text style={[styles.nativeName, lang.code === value && styles.activeText]}>
                    {lang.nativeName}
                  </Text>
                  <Text style={styles.langName}>{lang.name}</Text>
                </TouchableOpacity>
              ))}
              {filtered.length === 0 && (
                <Text style={styles.noResult}>No languages found</Text>
              )}
            </ScrollView>
          </TouchableOpacity>
        </TouchableOpacity>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  selector: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "rgba(255,255,255,0.05)",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  selectorText: { color: "#f7f8f8", fontSize: 14, flex: 1 },
  arrow: { color: "#62666d", fontSize: 16, marginLeft: 8 },
  overlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.6)",
    justifyContent: "center",
    alignItems: "center",
    padding: 24,
  },
  dropdown: {
    backgroundColor: "#141517",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)",
    width: "100%",
    maxWidth: 480,
    maxHeight: 480,
    overflow: "hidden",
  },
  searchInput: {
    backgroundColor: "rgba(255,255,255,0.05)",
    borderBottomWidth: 1,
    borderBottomColor: "rgba(255,255,255,0.08)",
    color: "#f7f8f8",
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 15,
  },
  list: { maxHeight: 400 },
  item: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: "rgba(255,255,255,0.04)",
  },
  itemActive: { backgroundColor: "rgba(113,112,255,0.1)" },
  nativeName: { color: "#f7f8f8", fontSize: 14, fontWeight: "500", flex: 1 },
  activeText: { color: "#7170ff" },
  langName: { color: "#62666d", fontSize: 13 },
  noResult: { color: "#62666d", textAlign: "center", padding: 24, fontSize: 14 },
});
