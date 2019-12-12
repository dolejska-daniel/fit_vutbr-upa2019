package upa.gui;

import upa.Application;
import upa.db.Connection;
import upa.db.entity.Entry;
import upa.db.entity.Geometry;
import upa.db.entity.Image;
import upa.gui.listener.GeometryRectMouseListener;
import upa.gui.model.EntryTableModel;
import upa.gui.model.GeometryTableModel;
import upa.gui.model.ImageTableModel;
import upa.utils.Convert;
import upa.utils.exception.ConversionException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JDialog
{
    private JPanel contentPane;
    private JPanel Controls;
    private JButton newEntryButton;
    private JTable entriesTable;
    private JTabbedPane tabbedPane;
    private JScrollPane imageDisplayPane;
    private JScrollPane geometryDisplayPane;
    private JButton removeEntryButton;
    private JTable imagesTable;
    private JButton newImageButton;
    private JButton removeImageButton;
    private JTable geometryTable;
    private JButton rectangleButton;
    private JButton button2;
    private JButton calculateXXXaButton;
    private JButton calculateXXXbButton;
    private JButton calculateXXXcButton;
    private JButton editImageButton;
    private JButton findSimilarButton;
    private JPanel geometryDisplay;
    private JComboBox geometryType;

    private JPanel imageDisplayWrapper;
    private JPanel imageDisplay;
    private BufferedImage imageToBeDisplayed;

    private int selectedEntryIndex = -1;
    private Entry selectedEntry = null;

    private int selectedImageIndex = -1;
    private Image selectedImage = null;

    private int selectedGeometryIndex = -1;
    private Geometry selectedGeometry = null;
    private Shape activeGeometry = null;

    private MouseAdapter currentGeometryListener;
    private JButton currentGeometryButton;
    private String previousGeometryButtonText;
    private ArrayList<JButton> geometryButtons = new ArrayList<>()
    {
        {
            add(rectangleButton);
            add(button2);
        }
    };


    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    public MainWindow()
    {
        Application.connection = Connection.CreateConnection();

        setLocationRelativeTo(null);
        setContentPane(contentPane);
        setSize(1200, 600);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        //-----------------------------------------------------dd--
        //  Entries table setup
        //-----------------------------------------------------dd--

        entriesTable.setModel(new EntryTableModel());
        entriesTable.doLayout();
        entriesTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;

            final DefaultListSelectionModel target = (DefaultListSelectionModel) e.getSource();
            // save current selection
            if (target.isSelectionEmpty())
                ClearEntrySelection();
            else
                SaveEntrySelection(target.getAnchorSelectionIndex());
        });
        entriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //-----------------------------------------------------dd--
        //  Entries buttons setup
        //-----------------------------------------------------dd--

        newEntryButton.addActionListener(e -> WindowManager.ShowNewEntryDialog());
        removeEntryButton.addActionListener(e -> {
            GetEntryTableModel().Delete(selectedEntryIndex);
            entriesTable.getSelectionModel().clearSelection();
        });

        //-----------------------------------------------------dd--
        //  Images table setup
        //-----------------------------------------------------dd--

        imagesTable.setModel(new ImageTableModel());
        imagesTable.doLayout();
        imagesTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;

            final DefaultListSelectionModel target = (DefaultListSelectionModel) e.getSource();
            // save current selection
            if (target.isSelectionEmpty())
                ClearImageSelection();
            else
                SaveImageSelection(target.getAnchorSelectionIndex());
        });
        imagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //-----------------------------------------------------dd--
        //  Image buttons setup
        //-----------------------------------------------------dd--

        newImageButton.addActionListener(e -> WindowManager.ShowNewImageDialog(selectedEntry.id));
        removeImageButton.addActionListener(e -> {
            GetImageTableModel().Delete(selectedEntryIndex);
            imagesTable.getSelectionModel().clearSelection();
        });
        editImageButton.addActionListener(e -> WindowManager.ShowEditImageDialog(selectedImage));
        findSimilarButton.addActionListener(e -> WindowManager.ShowSimilarImagesDialog(selectedImage));

        //-----------------------------------------------------dd--
        //  Image canvas setup
        //-----------------------------------------------------dd--

        imageDisplay = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.drawImage(imageToBeDisplayed, 0, 0, null);
            }
        };

        imageDisplayPane.getHorizontalScrollBar().setUnitIncrement(16);
        imageDisplayPane.getVerticalScrollBar().setUnitIncrement(16);
        imageDisplayPane.setViewportView(imageDisplay);

        //-----------------------------------------------------dd--
        //  Geometry table setup
        //-----------------------------------------------------dd--

        geometryTable.setModel(new GeometryTableModel());
        geometryTable.doLayout();
        geometryTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;

            final DefaultListSelectionModel target = (DefaultListSelectionModel) e.getSource();
            // save current selection
            if (target.isSelectionEmpty())
                ClearGeometrySelection();
            else
                SaveGeometrySelection(target.getAnchorSelectionIndex());
        });
        geometryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //-----------------------------------------------------dd--
        //  Geometry buttons setup
        //-----------------------------------------------------dd--

        //-----------------------------------------------------dd--
        //  Geometry canvas setup
        //-----------------------------------------------------dd--

        geometryDisplay = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics graphics)
            {
                super.paintComponent(graphics);
                Graphics2D graphics2D = (Graphics2D) graphics;
                GetGeometryTableModel().GetGeometryList().forEach(g -> drawGeometry(graphics2D, g));

                if (activeGeometry != null)
                {
                    graphics2D.setColor(new Color(19, 143, 189));
                    graphics2D.fill(activeGeometry);
                    graphics2D.setColor(new Color(115, 170, 192));
                    graphics2D.draw(activeGeometry);
                }
            }

            private void drawGeometry(Graphics2D graphics, Geometry g)
            {
                if (g.data == null)
                    return;

                Color borderColor = getBorderColorForGeometry(g);
                Color fillColor = getFillColorForGeometry(g);

                try
                {
                    Shape geometry = Convert.JGeometryToShape(g.data);

                    graphics.setColor(fillColor);
                    graphics.fill(geometry);
                    graphics.setColor(borderColor);
                    graphics.draw(geometry);
                }
                catch (ConversionException e)
                {
                    e.printStackTrace();
                }
            }

            private Color getBorderColorForGeometry(Geometry g)
            {
                // if this geometry is selected
                if (selectedGeometry == g)
                    return new Color(19, 143, 189);

                switch (g.type)
                {
                    case "Floor":
                        return new Color(150, 150, 150);
                    case "Wall":
                        return new Color(42, 42, 42);
                    case "Object":
                        return new Color(190, 190, 190);

                    default:
                        return new Color(0);
                }
            }

            private Color getFillColorForGeometry(Geometry g)
            {
                // if this geometry is selected
                if (selectedGeometry == g)
                    return new Color(115, 170, 192);

                switch (g.type)
                {
                    case "Floor":
                        return new Color(200, 200, 200);
                    case "Wall":
                        return new Color(92, 92, 92);
                    case "Object":
                        return new Color(240, 240, 240);

                    default:
                        return new Color(0);
                }
            }
        };
        geometryDisplayPane.setViewportView(geometryDisplay);
        geometryDisplay.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (activeGeometry != null)
                    return;

                boolean found = false;
                List<Geometry> geometryList = GetGeometryTableModel().GetGeometryList();
                for (int i = geometryList.size() - 1; i >= 0; --i)
                {
                    Geometry geometry = geometryList.get(i);
                    if (geometry.Shape().contains(e.getPoint()))
                    {
                        geometryTable.getSelectionModel().setSelectionInterval(i, i);
                        found = true;
                        break;
                    }
                }

                if (!found)
                    geometryTable.getSelectionModel().clearSelection();
            }
        });

        rectangleButton.addActionListener(e -> ActivateRectangleListener());
    }

    private void DeactivateCurrentListener()
    {
        if (currentGeometryListener != null)
        {
            geometryDisplay.removeMouseMotionListener(currentGeometryListener);
            geometryDisplay.removeMouseListener(currentGeometryListener);
        }

        if (currentGeometryButton != null)
        {
            currentGeometryButton.setText(previousGeometryButtonText);
            currentGeometryListener = null;
            currentGeometryButton = null;
            previousGeometryButtonText = null;
        }

        geometryButtons.forEach(b -> b.setEnabled(true));
    }

    private void ActivateListener(MouseAdapter listener, JButton button)
    {
        assert listener != null && button != null;

        currentGeometryListener = listener;
        geometryDisplay.addMouseMotionListener(listener);
        geometryDisplay.addMouseListener(listener);

        currentGeometryButton = button;
        previousGeometryButtonText = currentGeometryButton.getText();
        currentGeometryButton.setText("Stop");

        geometryButtons.forEach(b -> {
            if (b == button)
                return;

            b.setEnabled(false);
        });

        // TODO: Work with type
    }

    private void ActivateRectangleListener()
    {
        if (currentGeometryButton == rectangleButton)
            DeactivateCurrentListener();
        else
            ActivateListener(new GeometryRectMouseListener(this), rectangleButton);
    }


    //=====================================================================dd==
    // CUSTOM HELPER METHODS
    //=====================================================================dd==

    //-----------------------------------------------------dd--
    //  Entries table methods
    //-----------------------------------------------------dd--

    public EntryTableModel GetEntryTableModel()
    {
        return (EntryTableModel) entriesTable.getModel();
    }

    private void SaveEntrySelection(final int index)
    {
        selectedEntryIndex = index;
        selectedEntry = GetEntryTableModel().Get(index);

        // enable relevant objects
        removeEntryButton.setEnabled(true);
        newImageButton.setEnabled(true);
        tabbedPane.setEnabled(true);

        // reload image table
        GetImageTableModel().SetEntryId(selectedEntry.id);
        GetGeometryTableModel().SetEntryId(selectedEntry.id);
        // TODO: Reload geometry table
    }

    private void ClearEntrySelection()
    {
        selectedEntryIndex = -1;
        selectedEntry = null;

        // disable relevant objects
        removeEntryButton.setEnabled(false);
        newImageButton.setEnabled(false);
        tabbedPane.setEnabled(false);

        // reload image table
        GetImageTableModel().SetEntryId(-1);
        GetGeometryTableModel().SetEntryId(-1);
    }

    //-----------------------------------------------------dd--
    //  Images table methods
    //-----------------------------------------------------dd--

    public ImageTableModel GetImageTableModel()
    {
        return (ImageTableModel) imagesTable.getModel();
    }

    private void SaveImageSelection(final int index)
    {
        selectedImageIndex = index;
        selectedImage = GetImageTableModel().Get(index);

        // enable remove button
        editImageButton.setEnabled(true);
        findSimilarButton.setEnabled(true);
        removeImageButton.setEnabled(true);

        SetImageToBeDisplayed(selectedImage);
    }

    private void ClearImageSelection()
    {
        selectedImageIndex = -1;
        selectedImage = null;

        // disable remove button
        editImageButton.setEnabled(false);
        findSimilarButton.setEnabled(false);
        removeImageButton.setEnabled(false);

        ReloadImageToBeDisplayed();
    }

    private void SetImageToBeDisplayed(Image image)
    {
        try
        {
            imageToBeDisplayed = ImageIO.read(image.image.getDataInStream());
            imageDisplay.setPreferredSize(new Dimension(imageToBeDisplayed.getWidth(), imageToBeDisplayed.getHeight()));

            setSize(getWidth() + 1, getHeight() + 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void ReloadImageToBeDisplayed()
    {
        if (this.selectedImage != null)
            SetImageToBeDisplayed(this.selectedImage);
        else
        {
            imageToBeDisplayed = null;
            imageDisplay.setPreferredSize(new Dimension(1, 1));
            setSize(getWidth() - 1, getHeight() - 1);
        }
    }

    //-----------------------------------------------------dd--
    //  Geometry table methods
    //-----------------------------------------------------dd--

    private GeometryTableModel GetGeometryTableModel()
    {
        return (GeometryTableModel) geometryTable.getModel();
    }

    private void SaveGeometrySelection(final int index)
    {
        selectedGeometryIndex = index;
        selectedGeometry = GetGeometryTableModel().Get(index);

        // TODO: disable remove button?

        geometryDisplay.repaint();
        geometryDisplayPane.repaint();
    }

    private void ClearGeometrySelection()
    {
        selectedGeometryIndex = -1;
        selectedGeometry = null;

        // TODO: disable remove button?

        geometryDisplay.repaint();
        geometryDisplayPane.repaint();
    }

    //-----------------------------------------------------dd--
    //  Active geometry methods
    //-----------------------------------------------------dd--

    public void SetActiveGeometry(Shape s)
    {
        activeGeometry = s;
        geometryDisplay.repaint();
    }

    public void RemoveActiveGeometry()
    {
        activeGeometry = null;
        geometryDisplay.repaint();
    }

    public void SaveActiveGeometry()
    {
        try
        {
            Geometry g = new Geometry();
            g.entry_id = selectedEntry.id;
            g.type = String.valueOf(geometryType.getSelectedItem());
            g.data = Convert.ShapeToJGeometry(activeGeometry);
            g.Create();

            // TODO: Save geometry
            // activeGeometry

            GetGeometryTableModel().Insert(g);
            RemoveActiveGeometry();
            geometryDisplay.repaint();
        }
        catch (ConversionException e)
        {
            e.printStackTrace();
        }
    }


    //=====================================================================dd==
    // CUSTOM HELPER METHODS
    //=====================================================================dd==

    private void onOK()
    {
        dispose();
    }

    private void onCancel()
    {
        Connection.CloseConnection(Application.connection);

        dispose();
    }
}
