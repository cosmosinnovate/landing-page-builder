import React, { useState } from 'react';
import {
  DndContext,
  DragEndEvent,
  DragOverlay,
  DragStartEvent,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { ContentSection, SectionType, BlockType, ContentBlock } from '@/types';
import { createBlock, createSection, generateId } from '@/utils/helpers';
import SectionComponent from './SectionComponent';
import BlockPalette from './BlockPalette';
import { Settings as SettingsIcon, Eye, Save } from 'lucide-react';

interface PageBuilderProps {
  sections: ContentSection[];
  onChange: (sections: ContentSection[]) => void;
  onSave: () => void;
  onPreview: () => void;
}

const PageBuilder: React.FC<PageBuilderProps> = ({
  sections,
  onChange,
  onSave,
  onPreview,
}) => {
  const [activeId, setActiveId] = useState<string | null>(null);
  const [selectedSection, setSelectedSection] = useState<string | null>(null);
  const [selectedBlock, setSelectedBlock] = useState<string | null>(null);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    })
  );

  const handleDragStart = (event: DragStartEvent) => {
    setActiveId(event.active.id as string);
  };

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    setActiveId(null);

    if (!over) return;

    // Handle adding new blocks from palette
    if (active.id.toString().startsWith('palette-')) {
      const blockType = active.id.toString().replace('palette-', '') as BlockType;
      const sectionId = over.id as string;

      const newBlock = createBlock(blockType, getDefaultContent(blockType));
      addBlockToSection(sectionId, newBlock);
      return;
    }

    // Handle reordering sections or blocks
    if (active.id !== over.id) {
      const activeSection = sections.find((s) =>
        s.blocks.some((b) => b.id === active.id)
      );

      if (activeSection) {
        // Reordering blocks within a section
        reorderBlocks(activeSection.id, active.id as string, over.id as string);
      } else {
        // Reordering sections
        reorderSections(active.id as string, over.id as string);
      }
    }
  };

  const getDefaultContent = (type: BlockType): Record<string, any> => {
    switch (type) {
      case BlockType.HEADING:
        return { text: 'New Heading', level: 2 };
      case BlockType.PARAGRAPH:
        return { text: 'Add your content here...' };
      case BlockType.IMAGE:
        return { src: '', alt: '', width: '100%' };
      case BlockType.BUTTON:
        return { text: 'Click me', url: '#', variant: 'primary' };
      case BlockType.LIST:
        return { items: ['Item 1', 'Item 2', 'Item 3'], ordered: false };
      case BlockType.SPACER:
        return { height: '2rem' };
      case BlockType.DIVIDER:
        return { style: 'solid', color: '#e5e7eb' };
      default:
        return {};
    }
  };

  const addBlockToSection = (sectionId: string, block: ContentBlock) => {
    const newSections = sections.map((section) => {
      if (section.id === sectionId) {
        return {
          ...section,
          blocks: [...section.blocks, block],
        };
      }
      return section;
    });
    onChange(newSections);
  };

  const reorderSections = (activeId: string, overId: string) => {
    const oldIndex = sections.findIndex((s) => s.id === activeId);
    const newIndex = sections.findIndex((s) => s.id === overId);

    if (oldIndex === -1 || newIndex === -1) return;

    const newSections = [...sections];
    const [movedSection] = newSections.splice(oldIndex, 1);
    newSections.splice(newIndex, 0, movedSection);

    onChange(newSections);
  };

  const reorderBlocks = (sectionId: string, activeBlockId: string, overBlockId: string) => {
    const newSections = sections.map((section) => {
      if (section.id === sectionId) {
        const blocks = [...section.blocks];
        const oldIndex = blocks.findIndex((b) => b.id === activeBlockId);
        const newIndex = blocks.findIndex((b) => b.id === overBlockId);

        if (oldIndex === -1 || newIndex === -1) return section;

        const [movedBlock] = blocks.splice(oldIndex, 1);
        blocks.splice(newIndex, 0, movedBlock);

        return { ...section, blocks };
      }
      return section;
    });
    onChange(newSections);
  };

  const addSection = (type: SectionType) => {
    const newSection = createSection(type);
    onChange([...sections, newSection]);
  };

  const deleteSection = (sectionId: string) => {
    onChange(sections.filter((s) => s.id !== sectionId));
  };

  const updateSection = (sectionId: string, updates: Partial<ContentSection>) => {
    const newSections = sections.map((section) =>
      section.id === sectionId ? { ...section, ...updates } : section
    );
    onChange(newSections);
  };

  const updateBlock = (
    sectionId: string,
    blockId: string,
    updates: Partial<ContentBlock>
  ) => {
    const newSections = sections.map((section) => {
      if (section.id === sectionId) {
        return {
          ...section,
          blocks: section.blocks.map((block) =>
            block.id === blockId ? { ...block, ...updates } : block
          ),
        };
      }
      return section;
    });
    onChange(newSections);
  };

  const deleteBlock = (sectionId: string, blockId: string) => {
    const newSections = sections.map((section) => {
      if (section.id === sectionId) {
        return {
          ...section,
          blocks: section.blocks.filter((b) => b.id !== blockId),
        };
      }
      return section;
    });
    onChange(newSections);
  };

  return (
    <div className="flex h-full">
      <DndContext
        sensors={sensors}
        onDragStart={handleDragStart}
        onDragEnd={handleDragEnd}
      >
        {/* Left Sidebar - Block Palette */}
        <div className="w-64 bg-white border-r border-gray-200 overflow-y-auto">
          <div className="p-4">
            <h3 className="text-sm font-semibold text-gray-900 mb-4">Components</h3>
            <BlockPalette />

            <div className="mt-6">
              <h3 className="text-sm font-semibold text-gray-900 mb-4">Sections</h3>
              <div className="space-y-2">
                {Object.values(SectionType).map((type) => (
                  <button
                    key={type}
                    onClick={() => addSection(type)}
                    className="w-full px-3 py-2 text-sm text-left bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors"
                  >
                    {type}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Main Canvas */}
        <div className="flex-1 overflow-y-auto bg-gray-50">
          <div className="p-8">
            <div className="max-w-5xl mx-auto">
              <SortableContext
                items={sections.map((s) => s.id)}
                strategy={verticalListSortingStrategy}
              >
                {sections.length === 0 ? (
                  <div className="text-center py-20 bg-white rounded-lg border-2 border-dashed border-gray-300">
                    <SettingsIcon className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                    <h3 className="text-lg font-medium text-gray-900 mb-2">
                      Start building your page
                    </h3>
                    <p className="text-gray-600 mb-6">
                      Add sections from the left sidebar to get started
                    </p>
                  </div>
                ) : (
                  sections.map((section) => (
                    <SectionComponent
                      key={section.id}
                      section={section}
                      isSelected={selectedSection === section.id}
                      onSelect={() => setSelectedSection(section.id)}
                      onUpdate={(updates) => updateSection(section.id, updates)}
                      onDelete={() => deleteSection(section.id)}
                      onUpdateBlock={(blockId, updates) =>
                        updateBlock(section.id, blockId, updates)
                      }
                      onDeleteBlock={(blockId) => deleteBlock(section.id, blockId)}
                      selectedBlockId={selectedBlock}
                      onSelectBlock={setSelectedBlock}
                    />
                  ))
                )}
              </SortableContext>
            </div>
          </div>
        </div>

        {/* Right Sidebar - Properties Panel */}
        <div className="w-80 bg-white border-l border-gray-200 overflow-y-auto">
          <div className="p-4">
            <div className="flex space-x-2 mb-6">
              <button onClick={onPreview} className="flex-1 btn-secondary flex items-center justify-center space-x-2">
                <Eye className="w-4 h-4" />
                <span>Preview</span>
              </button>
              <button onClick={onSave} className="flex-1 btn-primary flex items-center justify-center space-x-2">
                <Save className="w-4 h-4" />
                <span>Save</span>
              </button>
            </div>

            {selectedBlock ? (
              <div>
                <h3 className="text-sm font-semibold text-gray-900 mb-4">
                  Block Properties
                </h3>
                <p className="text-sm text-gray-600">
                  Configure the selected block properties here
                </p>
              </div>
            ) : selectedSection ? (
              <div>
                <h3 className="text-sm font-semibold text-gray-900 mb-4">
                  Section Properties
                </h3>
                <p className="text-sm text-gray-600">
                  Configure the selected section properties here
                </p>
              </div>
            ) : (
              <div className="text-center py-8">
                <SettingsIcon className="w-12 h-12 text-gray-400 mx-auto mb-3" />
                <p className="text-sm text-gray-600">
                  Select a section or block to edit its properties
                </p>
              </div>
            )}
          </div>
        </div>

        <DragOverlay>
          {activeId ? (
            <div className="bg-white p-4 rounded-lg shadow-lg border border-primary-500">
              Dragging...
            </div>
          ) : null}
        </DragOverlay>
      </DndContext>
    </div>
  );
};

export default PageBuilder;

