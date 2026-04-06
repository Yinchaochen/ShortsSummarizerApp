import { useEffect, useRef } from "react";
import { Animated, StyleSheet, View } from "react-native";
import { LinearGradient } from "expo-linear-gradient";

export default function BreathingBackground() {
  const anim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(anim, { toValue: 1, duration: 4000, useNativeDriver: true }),
        Animated.timing(anim, { toValue: 0, duration: 4000, useNativeDriver: true }),
      ])
    ).start();
  }, []);

  const opacity = anim.interpolate({ inputRange: [0, 1], outputRange: [0.4, 0.75] });

  return (
    <View style={StyleSheet.absoluteFill} pointerEvents="none">
      {/* Deep base */}
      <View style={[StyleSheet.absoluteFill, { backgroundColor: "#08090a" }]} />

      {/* Top-left indigo bloom */}
      <Animated.View style={[styles.bloom, styles.bloomTopLeft, { opacity }]}>
        <LinearGradient
          colors={["#7170ff", "transparent"]}
          style={StyleSheet.absoluteFill}
          start={{ x: 0, y: 0 }}
          end={{ x: 1, y: 1 }}
        />
      </Animated.View>

      {/* Bottom-right purple bloom */}
      <Animated.View style={[styles.bloom, styles.bloomBottomRight, { opacity }]}>
        <LinearGradient
          colors={["#a855f7", "transparent"]}
          style={StyleSheet.absoluteFill}
          start={{ x: 1, y: 1 }}
          end={{ x: 0, y: 0 }}
        />
      </Animated.View>

      {/* Subtle center glow */}
      <Animated.View style={[styles.bloom, styles.bloomCenter, { opacity: anim.interpolate({ inputRange: [0, 1], outputRange: [0.05, 0.12] }) }]}>
        <LinearGradient
          colors={["#7170ff", "transparent"]}
          style={StyleSheet.absoluteFill}
          start={{ x: 0.5, y: 0.5 }}
          end={{ x: 1, y: 1 }}
        />
      </Animated.View>
    </View>
  );
}

const styles = StyleSheet.create({
  bloom: {
    position: "absolute",
    borderRadius: 9999,
    overflow: "hidden",
  },
  bloomTopLeft: {
    width: 500,
    height: 500,
    top: -200,
    left: -200,
  },
  bloomBottomRight: {
    width: 400,
    height: 400,
    bottom: -150,
    right: -150,
  },
  bloomCenter: {
    width: 600,
    height: 600,
    top: "20%",
    left: "-10%",
  },
});
