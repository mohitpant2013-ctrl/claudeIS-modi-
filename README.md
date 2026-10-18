# LowEnd Renderer (v0.5)
Fabric 1.16.5, Java 8 target. Built by GitHub Actions (no gradlew needed). Full plan: see the Master Plan doc.

## Modules
| Module | What it does | Config key |
|---|---|---|
| Overlay + GL probe | FPS / ms / 1% low / GL flags / hook status. F8 toggles | overlay |
| Entity culling | Skips far mobs and items (players, projectiles, riders, glowing, huge mobs never skipped) | entityCulling, entityDistance, otherEntityDistance |
| Block-entity culling | Skips far chests/signs/banners (beacons stay) | blockEntityCulling, blockEntityDistance |
| Governor | Auto quality levels 0-4 based on frame time vs target | governor, targetFps |
| Particle throttle | Drops a share of particles at higher levels (crit/enchant-hit/damage kept) | particleThrottle |
| Smart animations | Animated textures update less often at higher levels | smartAnimations |
| Simple lighting | Turns off smooth lighting / AO so chunk meshing is cheaper | forceSimpleLighting |
| Clouds / weather off | Governor stops drawing clouds (level >= cloudsOffLevel, default 2) and rain/snow (weatherOffLevel, default 3). 5 = never | cloudsOffLevel, weatherOffLevel |

Config file: .minecraft/config/lowend.properties (created on first launch). Set a key to false to disable it.

## Reading the overlay
- Line 3: `governor L2/4` is the current quality level; `culled E/BE` is entities / block entities skipped last frame.
- Line 4: `hooks: ent ok be ok part ? anim ?` shows which mixin hooks have run. `?` means not seen yet
  (particles/animations only show up once they happen). If something stays `?` after a minute of normal play,
  its mixin target name is probably wrong: send latest.log (search "LowEnd").

## Build
Push to GitHub -> Actions tab -> latest "build" run -> download artifact `lowend-renderer-jar`.
Use the jar that does NOT end in -sources or -dev. Needs Fabric API 0.42.0+1.16.

## Export Minecraft source (for exact method names)
Actions tab -> "build" workflow -> Run workflow. When done, download artifact `minecraft-sources`
and send it to Claude. This lets the next mixins be written against the real code.
| Chunk builder thread cap | Limits chunk-mesh worker threads (default 2) so they don't fight the render thread | chunkBuilderThreads (0 = vanilla) |
| Leaf culling (EXPERIMENTAL, off) | Hides faces between touching leaves; hollow canopy interior | leafCulling |
| Bottleneck probe | F9 shows how long the CPU waits for the GPU: GPU-bound vs CPU-bound | (key only) |

Also see recommended-jvm-args.txt.

## Not built yet (needs the exported Minecraft source to write safely)
Chunk rebuild time budget, look-ahead chunk priority, LOD ring, dynamic resolution,
S3TC/PBO, CIT. See the Master Plan doc for the order.
