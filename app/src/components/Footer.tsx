import { View, TouchableOpacity, StyleSheet, Linking } from "react-native";
import { FontAwesome } from "@expo/vector-icons";

const SOCIAL_LINKS = [
  { icon: "github" as const,   url: "https://github.com/Yinchaochen",                              label: "GitHub" },
  { icon: "medium" as const,   url: "https://medium.com/@lisumchen",                               label: "Medium" },
  { icon: "youtube" as const,  url: "https://www.youtube.com/@LisumChen",                          label: "YouTube" },
  { icon: "twitter" as const,  url: "https://x.com/lisum292",                                      label: "X" },
  { icon: "linkedin" as const, url: "https://www.linkedin.com/in/yinchao-chen-848038308",          label: "LinkedIn" },
];

export default function Footer() {
  return (
    <View style={styles.footer}>
      <View style={styles.divider} />
      <View style={styles.icons}>
        {SOCIAL_LINKS.map((item) => (
          <TouchableOpacity
            key={item.label}
            style={styles.iconButton}
            onPress={() => Linking.openURL(item.url)}
            accessibilityLabel={item.label}
          >
            <FontAwesome name={item.icon} size={18} color="#62666d" />
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  footer: {
    marginTop: 48,
    alignItems: "center",
  },
  divider: {
    width: "100%",
    height: 1,
    backgroundColor: "rgba(255,255,255,0.06)",
    marginBottom: 24,
  },
  icons: {
    flexDirection: "row",
    gap: 8,
  },
  iconButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: "rgba(255,255,255,0.04)",
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.07)",
    alignItems: "center",
    justifyContent: "center",
  },
});
