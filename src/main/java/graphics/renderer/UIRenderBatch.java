package graphics.renderer;

import components.SpriteRenderer;
import graphics.Primitive;
import graphics.ShaderDatatype;
import graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ui.UIComponentRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Azurite</h1>
 * Used to render UIComponentRenderers, which are rendered as {@code Primitive.QUAD}s
 * with textures.
 *
 * @see RenderBatch
 * @see UIRenderer
 */
public class UIRenderBatch extends RenderBatch {
    private final List<UIComponentRenderer> componentRenderers;

    /**
     * Create a default type render batch
     *
     * @param maxBatchSize maximum number of componentRenderers in the batch
     * @param zIndex       zIndex of the batch. Used for sorting.
     */
    UIRenderBatch(int maxBatchSize, int zIndex) {
        super(maxBatchSize, zIndex, Primitive.QUAD, ShaderDatatype.FLOAT2, ShaderDatatype.FLOAT4, ShaderDatatype.FLOAT2, ShaderDatatype.FLOAT);
        this.componentRenderers = new ArrayList<>();
        this.primitiveVertices = new float[vertexCount * 4];
    }

    /**
     * This function figures out how to add vertices with an origin at the top left
     *
     * @param index  index of the primitive to be loaded
     * @param offset offset of where the primitive should start being added to the array
     */
    @Override
    protected void loadVertexProperties(int index, int offset) {
        UIComponentRenderer componentRenderer = this.componentRenderers.get(index);
        Vector4f color = componentRenderer.getColorVector();
        Vector2f[] textureCoordinates = componentRenderer.getTexCoords();

        int textureID;
        if (componentRenderer.getTexture() != null)
            textureID = addTexture(componentRenderer.getTexture());
        else
            textureID = 0;

        int primitiveVerticesOffset = 0;

        // Add vertex with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 1:
                    yAdd = 0.0f;
                    break;
                case 2:
                    xAdd = 0.0f;
                    break;
                case 3:
                    yAdd = 1.0f;
                    break;
            }

            // Load position
            Vector3f loc = componentRenderer.getReadOnlyLocation();
            Vector2f scale = componentRenderer.getSize();

            float scaledX = (xAdd * scale.x);
            float scaledY = (yAdd * scale.y);

            data[offset] = loc.x + scaledX;
            data[offset + 1] = loc.y + scaledY;

            primitiveVertices[primitiveVerticesOffset] = data[offset];
            primitiveVertices[primitiveVerticesOffset + 1] = data[offset + 1];

            // Load color
            data[offset + 2] = color.x; // Red
            data[offset + 3] = color.y; // Green
            data[offset + 4] = color.z; // Blue
            data[offset + 5] = color.w; // Alpha

            primitiveVertices[primitiveVerticesOffset + 2] = data[offset + 2];
            primitiveVertices[primitiveVerticesOffset + 3] = data[offset + 3];
            primitiveVertices[primitiveVerticesOffset + 4] = data[offset + 4];
            primitiveVertices[primitiveVerticesOffset + 5] = data[offset + 5];

            // Load texture coordinates
            data[offset + 6] = textureCoordinates[i].x;
            data[offset + 7] = textureCoordinates[i].y;

            primitiveVertices[primitiveVerticesOffset + 6] = data[offset + 6];
            primitiveVertices[primitiveVerticesOffset + 7] = data[offset + 7];

            // Load texture ID
            data[offset + 8] = textureID;

            primitiveVertices[primitiveVerticesOffset + 8] = data[offset + 8];

            offset += vertexCount;
            primitiveVerticesOffset += vertexCount;
        }
    }

    /**
     * Checks if any componentRenderer is dirty (has changed any of its properties).
     * If so, resets its data in the data[] via load().
     * <p>
     * Calls the RenderBatch::updateBuffer method to re-upload the data if required
     * </p>
     */
    @Override
    public void updateBuffer() {
        for (int i = 0; i < componentRenderers.size(); i++) {
            if (componentRenderers.get(i).isDirty()) {
                // Create map for the dirty quad starting at its offset and ending in its length
                super.updateBuffer(i);
                componentRenderers.get(i).setClean();
            }
        }
    }

    /**
     * Adds a componentRenderer to this batch
     *
     * @param componentRenderer componentRenderer to be added
     * @return if the componentRenderer was successfully added to the batch
     */
    public boolean addSprite(UIComponentRenderer componentRenderer) {
        // If the batch already contains the componentRenderer don't add it to any other batch
        if (componentRenderers.contains(componentRenderer)) return true;

        // If the batch still has room, and is at the same z index as the componentRenderer, then add it to the batch
        if (hasRoomLeft() && zIndex() == componentRenderer.gameObject.zIndex()) {
            Texture tex = componentRenderer.getTexture();
            if (tex == null || (hasTexture(tex) || hasTextureRoom())) {
                // Get the index and add the renderObject
                componentRenderers.add(componentRenderer);
//                componentRenderer.setLocation(this, componentRenderers.size() - 1);

                // Add properties to local vertices array
                load(componentRenderers.size() - 1);

                if (componentRenderers.size() >= this.maxBatchSize) {
                    this.hasRoom = false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the given componentRenderer from the batch
     *
     * @param componentRenderer componentRenderer to be removed
     */
    public void removeSprite(SpriteRenderer componentRenderer) {
        // Confirm this componentRenderer has been added to this batch
        if (componentRenderer.getBatch() == this) {
            // Remove the componentRenderer from the list
            int index = componentRenderers.indexOf(componentRenderer);
            componentRenderers.remove(index);

            // Set Indices of the componentRenderers to the new indices
            for (int i = index; i < componentRenderers.size(); i++) {
                SpriteRenderer spr = componentRenderers.get(i);
                spr.setLocation(this, i);
                spr.markDirty();
            }

            // Call Remove with the componentRenderers index to remove it from the data buffer
            remove(componentRenderer.getIndex());
        }
    }
}