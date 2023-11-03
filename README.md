# entity-decoder
Entity Decoder allows to you to add support for Entity (de)serialization and basic Vanilla de(serialization) for Entities and Living Entities for [Minestom](https://github.com/Minestom/Minestom).

## How implement it? 
It's very simple. For this example we will use the Vanilla Entity Expansion included in the project.

### The regionalized entity file format
This is an appropiate way to store entities with an Instance that uses the Anvil File Format. You can create
your custom way to save te entities data implementing the [ChunkEntitiesHandler](https://github.com/Ynverxe/entity-decoder/blob/main/src/main/java/com/github/ynverxe/entitydecoder/handler/ChunkEntitiesHandler.java).

This format stores the entity data of each [Region](https://minecraft.fandom.com/wiki/Region_file_format) of the world
on individual .NBT files in a directory separated from the world data.

Directory structure:

```
└── world folder/
    ├── world data
    └── entities/
        └── entities.regionX.regionZ.nbt <- this file stores all entities of 32x32 chunks.
```

NBT Structure:

```
TAG_Compound {
  "chunkX:chunkZ": TAG_List[
    TAG_Compound {
      some entity data...
    }
  ]
}
```

With this in mind, we can load and save basic entity properties [(Entity & LivingEntity)](https://minecraft.fandom.com/wiki/Entity_format#Entity_Format) for a world that uses the Anvil format with the following code:

```java
public static void main(String[] args) {
    // some stuff...

    InstanceContainer container = MinecraftServer.getInstanceManager()
      .createInstanceContainer();

    EntityExpansionManager entityExpansionManager = createExpansionManager();
    IChunkLoader loader = createRegionalizedLoader(entityExpansionManager, Path.of("world"));

    container.setChunkLoader(loader);
  }

  private static EntityExpansionManager createExpansionManager() {
    // Create an EntityExpansionManager will give you more control of every entity type serialization
    // than only use a single EntityExpansion
    EntityExpansionManager entityExpansionManager = new EntityExpansionManager();

    EntityExpansion expansion = VanillaEntityExpansion.make(EntityFactory.ENTITY_FACTORY, true);
    entityExpansionManager.setFallbackExpansion(expansion); // This expansion will be used for all Entities

    entityExpansionManager.registerExpansion(EntityType.PLAYER, EntityExpansion.EMPTY); // Prevent process players data (optional)

    return entityExpansionManager;
  }

  private static IChunkLoader createRegionalizedLoader(EntityExpansion expansion, Path instancePath) {
    CompressionIOFactory compressionIOFactory = //... (optional, use CompressionIOFactory.IDENTITY if you don't want compression for region files)
    return EntityChunkLoader.newRegionalizedAnvilLoader(compressionIOFactory, expansion, instancePath);
  }
```

## Some util information
Entity Data format: https://minecraft.fandom.com/wiki/Entity_format
Region Format: https://minecraft.fandom.com/wiki/Region_file_format

## Licence
### entity-decoder © 2023 by Ynverxe is licensed under Attribution-ShareAlike 4.0 International. 
